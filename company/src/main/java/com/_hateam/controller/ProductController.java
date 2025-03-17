package com._hateam.controller;

import com._hateam.common.dto.ResponseDto;
import com._hateam.dto.ProductDto;
import com._hateam.dto.ProductRequestDto;
import com._hateam.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/companies/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 새로운 허브 루트 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<ProductDto>> createProduct(
            @RequestBody @Valid ProductRequestDto requestDto) {

        ProductDto productDto = productService.createProduct(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(HttpStatus.CREATED, productDto));
    }

    /**
     * 전체 허브 루트 목록 조회 (페이지네이션 지원)
     * 예시: GET /hub?page=0&size=10&sortBy=createdAt&isAsc=false
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<ProductDto>>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        List<ProductDto> productDto = productService.getAllProducts(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, productDto));
    }


    /**
     * 특정 허브 루트 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ProductDto>> getProduct(@PathVariable UUID id) {
        ProductDto productDto = productService.getProduct(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, productDto));
    }

    /**
     * 특정 허브 루트 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto<ProductDto>> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Valid ProductRequestDto requestDto) {
        ProductDto productDto = productService.updateProduct(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, productDto));
    }

    /**
     * 특정 허브 루트 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, "Product deleted successfully"));
    }
}
