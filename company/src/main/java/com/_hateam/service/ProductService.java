package com._hateam.service;

import com._hateam.dto.ProductDto;
import com._hateam.dto.ProductRequestDto;
import com._hateam.entity.Product;
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
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductDto createProduct(ProductRequestDto requestDto) {
        validateDuplicateHub(requestDto);
        Product Product = createHubEntity(requestDto);
        productRepository.save(Product);
        return ProductDto.hubToHubDto(Product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts(int page, int size, String sortBy, boolean isAsc) {
        List<Product> ProductList = hubInfoPaging(page, size, sortBy, isAsc);
        return ProductList.stream()
                .map(ProductDto::hubToHubDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID id) {
        Product Product = findHub(id);
        return ProductDto.hubToHubDto(Product);
    }

    @Transactional
    public ProductDto updateProduct(UUID id, ProductRequestDto requestDto) {
        Product Product = findHub(id);
        updateHubInfo(Product, requestDto);
        return ProductDto.hubToHubDto(Product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product Product = findHub(id);
        productRepository.delete(Product);
    }

    private void validateDuplicateHub(ProductRequestDto requestDto) {
        productRepository.findByNameAndDeletedAtIsNull(requestDto.getName()).ifPresent(m -> {
            throw new IllegalArgumentException("중복된 허브가 존재합니다.");
        });
    }

    private Product createHubEntity(ProductRequestDto requestDto) {
        Product Product = Product.builder().
                name(requestDto.getName()).
                address(requestDto.getAddress()).
                latitude(requestDto.getLatitude()).
                longitude(requestDto.getLongitude()).
                build();

//        // 시큐리티 컨텍스트에서 인증 정보를 가져와 createdBy 필드 설정
//            추후 시큐리티 적용시 다시 수정
//        CreatedInfo createdInfo = new CreatedInfo();
//        Product.setCreatedBy(createdInfo.getCreatedBy());
//        Product.setCreatedAt(createdInfo.getCreatedAt());
        return Product;
    }

    private List<Product> hubInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        // 10, 30, 50 외의 size는 기본 10으로 설정
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 전체 hub 수 조회
        long totalHubs = productRepository.count();
        // 전체 페이지 수 계산 (0페이지부터 시작하므로)
        int totalPages = (int) Math.ceil((double) totalHubs / size);

        // 요청한 페이지 번호가 전체 페이지 수 이상이면 예외 처리
        if (page >= totalPages && totalHubs > 0) {
            throw new IllegalArgumentException("요청한 페이지 번호(" + page + ")가 전체 페이지 수(" + totalPages + ")를 초과합니다.");
        }

        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable).getContent();
    }

    private Product findHub(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    private void updateHubInfo(Product Product, ProductRequestDto requestDto) {
        Product.setName(requestDto.getName());
        Product.setAddress(requestDto.getAddress());
        Product.setLatitude(requestDto.getLatitude());
        Product.setLongitude(requestDto.getLongitude());
    }
}
