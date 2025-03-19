package com._hateam.feign;

import com._hateam.common.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "CompanyController",
        url = "http://localhost:8080/companies"
)
public interface CompanyController {
    //    허브의 업체 조회
    @GetMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDto<CompanyDto>> getCompany(@PathVariable UUID id);

    @GetMapping
    ResponseEntity<ResponseDto<List<CompanyDto>>> getAllCompanies(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc);
}
