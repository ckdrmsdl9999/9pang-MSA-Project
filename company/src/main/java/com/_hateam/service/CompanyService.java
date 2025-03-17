package com._hateam.service;

import com._hateam.dto.CompanyDto;
import com._hateam.dto.CompanyRequestDto;
import com._hateam.entity.Company;
import com._hateam.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyDto createCompany(CompanyRequestDto requestDto) {
        // Company 엔티티 생성: 요청 DTO의 값으로 빌더를 사용
        Company company = Company.builder()
                .hubId(requestDto.getHubId())
                .userId(requestDto.getUserId())
                .companyName(requestDto.getCompanyName())
                .companyAddress(requestDto.getCompanyAddress())
                .companyType(requestDto.getCompanyType())
                .postalCode(requestDto.getPostalCode())
                .build();

        companyRepository.save(company);
        return CompanyDto.companyToCompanyDto(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyDto> getAllCompanies(int page, int size, String sortBy, boolean isAsc) {
        // 페이지 사이즈가 10, 30, 50 외의 값이면 기본 10으로 설정 (필요시)
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                        sortBy.equals("updatedAt") ? "updatedAt" : "createdAt"));
        List<Company> companyList = companyRepository.findAll(pageable).getContent();
        return companyList.stream()
                .map(CompanyDto::companyToCompanyDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyDto getCompany(UUID id) {
        Company company = findCompany(id);
        return CompanyDto.companyToCompanyDto(company);
    }

    @Transactional
    public CompanyDto updateCompany(UUID id, CompanyRequestDto requestDto) {
        Company company = findCompany(id);
        // 기존 Company 필드를 요청 DTO 값으로 업데이트
        company.setHubId(requestDto.getHubId());
        company.setUserId(requestDto.getUserId());
        company.setCompanyName(requestDto.getCompanyName());
        company.setCompanyAddress(requestDto.getCompanyAddress());
        company.setCompanyType(requestDto.getCompanyType());
        company.setPostalCode(requestDto.getPostalCode());
        return CompanyDto.companyToCompanyDto(company);
    }

    @Transactional
    public void deleteCompany(UUID id) {
        Company company = findCompany(id);
        companyRepository.delete(company);
    }

    private Company findCompany(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));
    }
}
