package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.CompanyCreateRequest;
import com.se191116.studymanagement.model.dto.request.CompanyUpdateRequest;
import com.se191116.studymanagement.model.dto.response.CompanyResponse;
import com.se191116.studymanagement.model.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyResponse toCompanyResponse(Company company);

    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Company toCompany(CompanyCreateRequest request);

    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateCompanyFromRequest(CompanyUpdateRequest request, @MappingTarget Company company);
}
