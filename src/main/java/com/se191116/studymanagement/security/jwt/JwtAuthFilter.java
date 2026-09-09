package com.se191116.studymanagement.security.jwt;

import com.se191116.studymanagement.exception.ErrorCode;
import com.se191116.studymanagement.security.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtEntryPoint jwtEntryPoint;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, JwtEntryPoint jwtEntryPoint) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.jwtEntryPoint = jwtEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "/api/auth/login".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = jwtService.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (ExpiredJwtException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute(JwtEntryPoint.AUTH_ERROR_CODE_ATTR, ErrorCode.EXPIRED_JWT_TOKEN);
                request.setAttribute(JwtEntryPoint.AUTH_ERROR_MESSAGE_ATTR, "JWT token has expired");
                jwtEntryPoint.commence(request, response, new CredentialsExpiredException("JWT token has expired", e));
                return;
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute(JwtEntryPoint.AUTH_ERROR_CODE_ATTR, ErrorCode.INVALID_JWT_TOKEN);
                request.setAttribute(JwtEntryPoint.AUTH_ERROR_MESSAGE_ATTR, "JWT token is invalid");
                jwtEntryPoint.commence(request, response, new BadCredentialsException("JWT token is invalid", e));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
