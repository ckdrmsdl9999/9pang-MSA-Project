package com._hateam.service;

import com._hateam.dto.ProductDto;
import com._hateam.dto.ProductRequestDto;
import com._hateam.entity.Company;
import com._hateam.entity.Product;
import com._hateam.repository.CompanyRepository;
import com._hateam.repository.ProductRepository;
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

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository; // Company 엔티티 조회를 위한 Repository

    @Transactional
    public ProductDto createHubRoute(ProductRequestDto requestDto) {
        // 출발지와 도착지 ID가 동일하면 예외 발생
        validateSourceAndDestinationDifferent(requestDto);

        // 출발지와 도착지 Company 객체 조회
        Company sourceCompany = getHubById(requestDto.getSourceHubId(), "출발지");
        Company destinationCompany = getHubById(requestDto.getDestinationHubId(), "도착지");

        // Product 엔티티 생성 (선택적 필드 포함)
        Product product = Product.builder()
                .sourceCompany(sourceCompany)
                .destinationCompany(destinationCompany)
                .distanceKm(requestDto.getDistanceKm())
                .estimatedTimeMinutes(requestDto.getEstimatedTimeMinutes())
                .build();

        productRepository.save(product);
        return ProductDto.fromEntity(product);
    }


    @Transactional(readOnly = true)
    public List<ProductDto> getAllHubRoutes(int page, int size, String sortBy, boolean isAsc) {
        // 페이징, 정렬 처리
        List<Product> productList = hubInfoPaging(page, size, sortBy, isAsc);
        return productList.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getHubRoute(UUID id) {
        Product product = findHubRoute(id);
        return ProductDto.fromEntity(product);
    }

    @Transactional
    public ProductDto updateHubRoute(UUID id, ProductRequestDto requestDto) {
        Product product = findHubRoute(id);

        // 출발지와 도착지 ID가 동일하면 예외 발생
        validateSourceAndDestinationDifferent(requestDto);

        // 출발지 허브 업데이트 (ID가 변경된 경우)
        if (requestDto.getSourceHubId() != null &&
                !requestDto.getSourceHubId().equals(product.getSourceCompany().getId())) {
            Company newSourceCompany = getHubById(requestDto.getSourceHubId(), "출발지");
            product.setSourceCompany(newSourceCompany);
        }

        // 도착지 허브 업데이트 (ID가 변경된 경우)
        if (requestDto.getDestinationHubId() != null &&
                !requestDto.getDestinationHubId().equals(product.getDestinationCompany().getId())) {
            Company newDestinationCompany = getHubById(requestDto.getDestinationHubId(), "도착지");
            product.setDestinationCompany(newDestinationCompany);
        }

        // 기타 필드 업데이트
        product.setDistanceKm(requestDto.getDistanceKm());
        product.setEstimatedTimeMinutes(requestDto.getEstimatedTimeMinutes());

        return ProductDto.fromEntity(product);
    }


    @Transactional
    public void deleteHubRoute(UUID id) {
        Product product = findHubRoute(id);
        productRepository.delete(product);
    }

    /**
     * 요청 DTO에서 출발지와 도착지 Company ID가 동일하면 예외 발생.
     */
    private void validateSourceAndDestinationDifferent(ProductRequestDto requestDto) {
        if (requestDto.getSourceHubId().equals(requestDto.getDestinationHubId())) {
            throw new IllegalArgumentException("출발지와 도착지는 동일할 수 없습니다.");
        }
    }

    private Company getHubById(UUID hubId, String hubType) {
        return companyRepository.findById(hubId)
                .orElseThrow(() -> new EntityNotFoundException(hubType + " 허브를 찾을 수 없습니다. ID: " + hubId));
    }


    private List<Product> hubInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        long totalRoutes = productRepository.count();
        int totalPages = (int) Math.ceil((double) totalRoutes / size);

        if (page >= totalPages && totalRoutes > 0) {
            throw new IllegalArgumentException("요청한 페이지 번호(" + page + ")가 전체 페이지 수(" + totalPages + ")를 초과합니다.");
        }

        // sortBy 파라미터가 "updatedAt"이면 updatedAt, 그 외는 createdAt으로 정렬
        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable).getContent();
    }

    private Product findHubRoute(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }
}
