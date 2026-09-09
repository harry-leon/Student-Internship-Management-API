package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.CompanyCreateRequest;
import com.se191116.studymanagement.model.dto.request.CompanyUpdateRequest;
import com.se191116.studymanagement.model.dto.response.CompanyResponse;
import com.se191116.studymanagement.model.entity.Company;
import com.se191116.studymanagement.model.mapper.CompanyMapper;
import com.se191116.studymanagement.repository.CompanyRepository;
import com.se191116.studymanagement.service.AuditLogService;
import com.se191116.studymanagement.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyCreateRequest request) {
        if (companyRepository.existsByCompanyName(request.getCompanyName())) {
            throw new ResourceConflictException("Company with name '" + request.getCompanyName() + "' already exists");
        }

        Company company = companyMapper.toCompany(request);
        Company saved = companyRepository.save(company);
        auditLogService.log(null, "CREATE_COMPANY", "COMPANY", saved.getCompanyId(), "Company created: " + saved.getCompanyName());
        return companyMapper.toCompanyResponse(saved);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Integer companyId, CompanyUpdateRequest request) {
        Company existing = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        Company nameOwner = companyRepository.findByCompanyName(request.getCompanyName()).orElse(null);
        if (nameOwner != null && !nameOwner.getCompanyId().equals(companyId)) {
            throw new ResourceConflictException("Company with name '" + request.getCompanyName() + "' already exists");
        }

        companyMapper.updateCompanyFromRequest(request, existing);
        Company updated = companyRepository.save(existing);
        auditLogService.log(null, "UPDATE_COMPANY", "COMPANY", updated.getCompanyId(), "Company updated: " + updated.getCompanyName());
        return companyMapper.toCompanyResponse(updated);
    }

    @Override
    @Transactional
    public CompanyResponse updateStatus(Integer companyId, Boolean isActive) {
        Company existing = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        existing.setIsActive(isActive);
        Company updated = companyRepository.save(existing);
        auditLogService.log(null, "UPDATE_COMPANY_STATUS", "COMPANY", updated.getCompanyId(), "Status set to: " + isActive);
        return companyMapper.toCompanyResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCompany(Integer companyId) {
        Company existing = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        existing.setIsActive(false);
        companyRepository.save(existing);
        auditLogService.log(null, "SOFT_DELETE_COMPANY", "COMPANY", companyId, "Company soft deleted");
    }

    @Override
    public Page<CompanyResponse> getCompanies(Boolean active, String search, Pageable pageable) {
        Page<Company> companies;
        if (search != null && !search.isBlank()) {
            companies = companyRepository.findByCompanyNameContainingIgnoreCase(search.trim(), pageable);
        } else if (active != null) {
            companies = companyRepository.findByIsActive(active, pageable);
        } else {
            companies = companyRepository.findAll(pageable);
        }
        return companies.map(companyMapper::toCompanyResponse);
    }

    @Override
    public CompanyResponse getCompanyById(Integer companyId) {
        Company existing = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));
        return companyMapper.toCompanyResponse(existing);
    }
}
