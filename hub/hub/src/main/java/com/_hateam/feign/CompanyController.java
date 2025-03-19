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
    //    허브의 소속 특정 업체 조회
    @GetMapping
    ResponseEntity<ResponseDto<CompanyDto>> getCompanyByCompanyIdAndHubId(
            @RequestParam(value = "companyId", defaultValue = "0") UUID companyId,
            @RequestParam(value = "hubId", defaultValue = "0" ) UUID hubId);

    //    허브의 소속 업체 전체 조회
    @GetMapping
    ResponseEntity<ResponseDto<List<CompanyDto>>> getCompaniesByHubId(
            @RequestParam(value = "hubId", defaultValue = "0") UUID id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) ;
}
