package com._hateam.delivery.feignClient;

import com._hateam.common.dto.ResponseDto;
import com._hateam.delivery.dto.response.CompanyClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/companies/{id}")
    ResponseEntity<ResponseDto<CompanyClientResponseDto>> getCompanyById(@PathVariable("id") UUID id);
}