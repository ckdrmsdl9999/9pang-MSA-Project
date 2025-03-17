package com._hateam.controller;

import com._hateam.common.dto.ResponseDto;
import com._hateam.dto.CompanyDto;
import com._hateam.dto.CompanyRequestDto;
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
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final ProductService productService;

    /**
     * 새로운 허브 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<CompanyDto>> createHub(
            @RequestBody @Valid CompanyRequestDto requestDto) {

        CompanyDto hub = productService.createHub(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(HttpStatus.CREATED, hub));
    }

    /**
     * 전체 허브 목록 조회 (페이지네이션 지원)
     * 예시: GET /hub?page=0&size=10&sortBy=createdAt&isAsc=false
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<CompanyDto>>> getAllHubs(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        List<CompanyDto> hubs = productService.getAllHubs(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, hubs));
    }


    /**
     * 특정 허브 허브상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<CompanyDto>> getHub(@PathVariable UUID id) {
        CompanyDto hub = productService.getHub(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, hub));
    }

    /**
     * 특정 허브 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto<CompanyDto>> updateHub(
            @PathVariable UUID id,
            @RequestBody @Valid CompanyRequestDto requestDto) {
        CompanyDto updateHub = productService.updateHub(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, updateHub));
    }

    /**
     * 특정 허브 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deleteHub(@PathVariable UUID id) {
        productService.deleteHub(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, "Company deleted successfully"));
    }
}
