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
public class ProductService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyDto createHub(CompanyRequestDto requestDto) {
        validateDuplicateHub(requestDto);
        Company company = createHubEntity(requestDto);
        companyRepository.save(company);
        return CompanyDto.hubToHubDto(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyDto> getAllHubs(int page, int size, String sortBy, boolean isAsc) {
        List<Company> companyList = hubInfoPaging(page, size, sortBy, isAsc);
        return companyList.stream()
                .map(CompanyDto::hubToHubDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CompanyDto getHub(UUID id) {
        Company company = findHub(id);
        return CompanyDto.hubToHubDto(company);
    }

    @Transactional
    public CompanyDto updateHub(UUID id, CompanyRequestDto requestDto) {
        Company company = findHub(id);
        updateHubInfo(company, requestDto);
        return CompanyDto.hubToHubDto(company);
    }

    @Transactional
    public void deleteHub(UUID id) {
        Company company = findHub(id);
        companyRepository.delete(company);
    }

    private void validateDuplicateHub(CompanyRequestDto requestDto) {
        companyRepository.findByNameAndDeletedAtIsNull(requestDto.getName()).ifPresent(m -> {
            throw new IllegalArgumentException("중복된 허브가 존재합니다.");
        });
    }

    private Company createHubEntity(CompanyRequestDto requestDto) {
        Company company = Company.builder().
                name(requestDto.getName()).
                address(requestDto.getAddress()).
                latitude(requestDto.getLatitude()).
                longitude(requestDto.getLongitude()).
                build();

//        // 시큐리티 컨텍스트에서 인증 정보를 가져와 createdBy 필드 설정
//            추후 시큐리티 적용시 다시 수정
//        CreatedInfo createdInfo = new CreatedInfo();
//        company.setCreatedBy(createdInfo.getCreatedBy());
//        company.setCreatedAt(createdInfo.getCreatedAt());
        return company;
    }

    private List<Company> hubInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        // 10, 30, 50 외의 size는 기본 10으로 설정
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 전체 hub 수 조회
        long totalHubs = companyRepository.count();
        // 전체 페이지 수 계산 (0페이지부터 시작하므로)
        int totalPages = (int) Math.ceil((double) totalHubs / size);

        // 요청한 페이지 번호가 전체 페이지 수 이상이면 예외 처리
        if (page >= totalPages && totalHubs > 0) {
            throw new IllegalArgumentException("요청한 페이지 번호(" + page + ")가 전체 페이지 수(" + totalPages + ")를 초과합니다.");
        }

        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return companyRepository.findAll(pageable).getContent();
    }

    private Company findHub(UUID id) {
        return companyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));
    }

    private void updateHubInfo(Company company, CompanyRequestDto requestDto) {
        company.setName(requestDto.getName());
        company.setAddress(requestDto.getAddress());
        company.setLatitude(requestDto.getLatitude());
        company.setLongitude(requestDto.getLongitude());
    }
}
