package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.CompanyCreateRequest;
import com.se191116.studymanagement.model.dto.request.CompanyStatusUpdateRequest;
import com.se191116.studymanagement.model.dto.request.CompanyUpdateRequest;
import com.se191116.studymanagement.model.dto.response.CompanyResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.service.CompanyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<CompanyResponse>>> getCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "companyId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CompanyResponse> companies = companyService.getCompanies(active, search, pageable);
        return ResponseEntity.ok(SuccessResponse.success(companies, "Companies retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{company_id}")
    public ResponseEntity<SuccessResponse<CompanyResponse>> getCompanyById(@PathVariable("company_id") Integer companyId) {
        return ResponseEntity.ok(SuccessResponse.success(companyService.getCompanyById(companyId), "Company retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<CompanyResponse>> createCompany(@RequestBody @Valid CompanyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(companyService.createCompany(request), "Company created successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{company_id}")
    public ResponseEntity<SuccessResponse<CompanyResponse>> updateCompany(
            @PathVariable("company_id") Integer companyId,
            @RequestBody @Valid CompanyUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(companyService.updateCompany(companyId, request), "Company updated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{company_id}/status")
    public ResponseEntity<SuccessResponse<CompanyResponse>> updateCompanyStatus(
            @PathVariable("company_id") Integer companyId,
            @RequestBody @Valid CompanyStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(companyService.updateStatus(companyId, request.getIsActive()), "Company status updated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{company_id}")
    public ResponseEntity<SuccessResponse<String>> deleteCompany(@PathVariable("company_id") Integer companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok(SuccessResponse.success("Company deleted successfully"));
    }
}
