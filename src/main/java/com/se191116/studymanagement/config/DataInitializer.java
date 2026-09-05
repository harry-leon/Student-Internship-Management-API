package com.se191116.studymanagement.config;

import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedUserIfNotFound("admin", "admin@fpt.edu.vn", "admin123", "Hệ Thống Admin", UserRole.ADMIN);
        seedUserIfNotFound("mentor1", "mentor1@fpt.edu.vn", "mentor123", "Dr. Le Thi B (Mentor)", UserRole.MENTOR);
        seedUserIfNotFound("student1", "student1@fpt.edu.vn", "student123", "Nguyen Van A (Student)", UserRole.STUDENT);
    }

    private void seedUserIfNotFound(String username, String email, String password, String fullName, UserRole role) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    user.setPasswordHash(passwordEncoder.encode(password));
                    user.setEmail(email);
                    user.setFullName(fullName);
                    user.setRole(role);
                    user.setIsActive(true);
                    userRepository.save(user);
                    log.info("Default {} account updated: username='{}'", role, username);
                },
                () -> {
                    User user = User.builder()
                            .username(username)
                            .email(email)
                            .passwordHash(passwordEncoder.encode(password))
                            .fullName(fullName)
                            .role(role)
                            .isActive(true)
                            .build();
                    userRepository.save(user);
                    log.info("Default {} account created: username='{}'", role, username);
                }
        );
    }
}
