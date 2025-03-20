package com._hateam.order.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.order.infrastructure.client.dto.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service", url = "${services.company.url}")
public interface CompanyClient {

    @GetMapping("/companies/{companyId}")
    ResponseDto<CompanyDto> getCompanyById(@PathVariable("companyId") UUID companyId);
}