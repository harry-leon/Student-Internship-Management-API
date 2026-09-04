package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.CompanyCreateRequest;
import com.se191116.studymanagement.model.dto.request.CompanyUpdateRequest;
import com.se191116.studymanagement.model.dto.response.CompanyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {
    Page<CompanyResponse> getCompanies(Boolean active, String search, Pageable pageable);
    CompanyResponse getCompanyById(Integer companyId);
    CompanyResponse createCompany(CompanyCreateRequest request);
    CompanyResponse updateCompany(Integer companyId, CompanyUpdateRequest request);
    CompanyResponse updateStatus(Integer companyId, Boolean isActive);
    void deleteCompany(Integer companyId);
}
