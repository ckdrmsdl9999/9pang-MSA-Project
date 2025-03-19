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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository; // Product의 소속 Company를 조회하기 위해

    @Transactional
    public ProductDto createProduct(ProductRequestDto requestDto) {
        // 중복 상품 체크
        validateDuplicateProduct(requestDto);
        Product product = createProductEntity(requestDto);
        productRepository.save(product);
        return ProductDto.productToProductDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts(int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                        sortBy.equals("updatedAt") ? "updatedAt" : "createdAt"));
        List<Product> productList = productRepository.findAll(pageable).getContent();
        return productList.stream()
                .map(ProductDto::productToProductDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID id) {
        Product product = findProduct(id);
        return ProductDto.productToProductDto(product);
    }

    public List<ProductDto> getProductsByHubId(UUID hubId) {
        // 1. hubId를 사용하여 회사 목록을 찾습니다.
        List<Company> companies = companyRepository.findByHubId(hubId);

        // 2. 회사 목록이 비어 있으면 null 또는 예외를 반환합니다.
        if (companies == null || companies.isEmpty()) {
            return null; // 또는 throw new CompanyNotFoundException("Companies not found for hubId: " + hubId);
        }

        // 3. 모든 회사의 제품 목록을 하나의 목록으로 합칩니다.
        List<Product> allProducts = new ArrayList<>();
        for (Company company : companies) {
            allProducts.addAll(company.getProducts());
        }

        // 4. 제품 목록을 ProductDto 목록으로 변환합니다.
        List<ProductDto> productDtoList = allProducts.stream()
                .map(ProductDto::productToProductDto)
                .collect(Collectors.toList());

        // 5. ProductDto 목록을 반환합니다.
        return productDtoList;
    }
    @Transactional
    public ProductDto updateProduct(UUID id, ProductRequestDto requestDto) {
        Product product = findProduct(id);

        // 만약 상품 이름이 변경되면 중복 체크 (필요 시)
        if (requestDto.getName() != null && !requestDto.getName().equals(product.getName())) {
            validateDuplicateProduct(requestDto);
        }

        // 소속 Company 업데이트 (변경된 경우)
        if (requestDto.getCompanyId() != null &&
                !requestDto.getCompanyId().equals(product.getCompany().getId())) {
            Company newCompany = companyRepository.findById(requestDto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + requestDto.getCompanyId()));
            product.setCompany(newCompany);
        }

        // 나머지 필드 업데이트
        product.setName(requestDto.getName());
        product.setQuantity(requestDto.getQuantity());
        product.setDescription(requestDto.getDescription());
        product.setPrice(requestDto.getPrice());

        return ProductDto.productToProductDto(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = findProduct(id);
        productRepository.delete(product);
    }

    // 중복 상품 체크: 동일한 이름의 상품이 존재하면 예외 발생
    private void validateDuplicateProduct(ProductRequestDto requestDto) {
        productRepository.findByNameAndDeletedAtIsNull(requestDto.getName())
                .ifPresent(p -> {
                    throw new IllegalArgumentException("중복된 상품이 존재합니다.");
                });
    }

    private Product createProductEntity(ProductRequestDto requestDto) {
        // 소속 Company 조회
        Company company = companyRepository.findById(requestDto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + requestDto.getCompanyId()));

        // Product 엔티티 생성: 요청 DTO의 값으로 빌더를 사용
        return Product.builder()
                .company(company)
                .name(requestDto.getName())
                .quantity(requestDto.getQuantity())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .build();
    }

    private Product findProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }


}
