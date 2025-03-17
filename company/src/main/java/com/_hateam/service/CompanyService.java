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
        // 출발지와 도착지 ID가 동일하면 예외 발생
        validateSourceAndDestinationDifferent(requestDto);

        // 출발지와 도착지 Company 객체 조회
        Company sourceCompany = getHubById(requestDto.getSourceHubId(), "출발지");
        Company destinationCompany = getHubById(requestDto.getDestinationHubId(), "도착지");

        // Company 엔티티 생성 (선택적 필드 포함)
        Company Company = Company.builder()
                .sourceCompany(sourceCompany)
                .destinationCompany(destinationCompany)
                .distanceKm(requestDto.getDistanceKm())
                .estimatedTimeMinutes(requestDto.getEstimatedTimeMinutes())
                .build();

        companyRepository.save(Company);
        return CompanyDto.fromEntity(Company);
    }


    @Transactional(readOnly = true)
    public List<CompanyDto> getAllCompanies(int page, int size, String sortBy, boolean isAsc) {
        // 페이징, 정렬 처리
        List<Company> CompanyList = hubInfoPaging(page, size, sortBy, isAsc);
        return CompanyList.stream()
                .map(CompanyDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyDto getCompany(UUID id) {
        Company Company = findCompany(id);
        return CompanyDto.fromEntity(Company);
    }

    @Transactional
    public CompanyDto updateCompany(UUID id, CompanyRequestDto requestDto) {
        Company Company = findCompany(id);

        // 출발지와 도착지 ID가 동일하면 예외 발생
        validateSourceAndDestinationDifferent(requestDto);

        // 출발지 허브 업데이트 (ID가 변경된 경우)
        if (requestDto.getSourceHubId() != null &&
                !requestDto.getSourceHubId().equals(Company.getSourceCompany().getId())) {
            Company newSourceCompany = getHubById(requestDto.getSourceHubId(), "출발지");
            Company.setSourceCompany(newSourceCompany);
        }

        // 도착지 허브 업데이트 (ID가 변경된 경우)
        if (requestDto.getDestinationHubId() != null &&
                !requestDto.getDestinationHubId().equals(Company.getDestinationCompany().getId())) {
            Company newDestinationCompany = getHubById(requestDto.getDestinationHubId(), "도착지");
            Company.setDestinationCompany(newDestinationCompany);
        }

        // 기타 필드 업데이트
        Company.setDistanceKm(requestDto.getDistanceKm());
        Company.setEstimatedTimeMinutes(requestDto.getEstimatedTimeMinutes());

        return CompanyDto.fromEntity(Company);
    }


    @Transactional
    public void deleteCompany(UUID id) {
        Company Company = findCompany(id);
        companyRepository.delete(Company);
    }

    /**
     * 요청 DTO에서 출발지와 도착지 Company ID가 동일하면 예외 발생.
     */
    private void validateSourceAndDestinationDifferent(CompanyRequestDto requestDto) {
        if (requestDto.getSourceHubId().equals(requestDto.getDestinationHubId())) {
            throw new IllegalArgumentException("출발지와 도착지는 동일할 수 없습니다.");
        }
    }

    private Company getHubById(UUID hubId, String hubType) {
        return companyRepository.findById(hubId)
                .orElseThrow(() -> new EntityNotFoundException(hubType + " 허브를 찾을 수 없습니다. ID: " + hubId));
    }


    private List<Company> hubInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        long totalRoutes = companyRepository.count();
        int totalPages = (int) Math.ceil((double) totalRoutes / size);

        if (page >= totalPages && totalRoutes > 0) {
            throw new IllegalArgumentException("요청한 페이지 번호(" + page + ")가 전체 페이지 수(" + totalPages + ")를 초과합니다.");
        }

        // sortBy 파라미터가 "updatedAt"이면 updatedAt, 그 외는 createdAt으로 정렬
        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return companyRepository.findAll(pageable).getContent();
    }

    private Company findCompany(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));
    }
}
