package com._hateam.controller;

import com._hateam.common.dto.ResponseDto;
import com._hateam.dto.CompanyDto;
import com._hateam.dto.CompanyRequestDto;
import com._hateam.service.CompanyService;
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

    private final CompanyService companyService;

    /**
     * 새로운 업체 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<CompanyDto>> createCompany(
            @RequestBody @Valid CompanyRequestDto requestDto) {

        CompanyDto company = companyService.createCompany(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(HttpStatus.CREATED, company));
    }

    /**
     * 전체 업체 목록 조회 (페이지네이션 지원)
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<CompanyDto>>> getAllCompanies(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        List<CompanyDto> companies = companyService.getAllCompanies(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, companies));
    }

    @GetMapping("/hub/{hubId}/{companyId}")
    public CompanyDto getCompanyByCompanyIdAndHubId(
            @PathVariable UUID hubId,
            @PathVariable UUID companyId) {
        CompanyDto company = companyService.getCompanyByCompanyIdAndHubId(companyId, hubId);
        return company;
    }

    @GetMapping("/hub/{hubId}")
    public List<CompanyDto> getCompaniesByHubId(
            @PathVariable UUID hubId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        List<CompanyDto> companies = companyService.getCompaniesByHubId(hubId, page, size, sortBy, isAsc);
        return companies;
    }

    /**
     * 특정 업체 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<CompanyDto>> getCompany(@PathVariable UUID id) {
        CompanyDto company = companyService.getCompany(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, company));
    }

    /**
     * 특정 업체 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto<CompanyDto>> updateCompany(
            @PathVariable UUID id,
            @RequestBody @Valid CompanyRequestDto requestDto) {
        CompanyDto updateCompany = companyService.updateCompany(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, updateCompany));
    }

    /**
     * 특정 업체 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, "Company deleted successfully"));
    }
}
