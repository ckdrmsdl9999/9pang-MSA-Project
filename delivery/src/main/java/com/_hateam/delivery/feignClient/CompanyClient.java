package com._hateam.delivery.feignClient;

import com._hateam.delivery.dto.response.CompanyClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service", url = "${company-service.url}")
public interface CompanyClient {

    @GetMapping("api/companies/{id}")
    CompanyClientResponseDto getCompanyById(@PathVariable("id") Long id);
}