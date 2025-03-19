package com._hateam.feign;

import com._hateam.common.dto.ResponseDto;
import com._hateam.dto.CompanyDto;
import com._hateam.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "HubController",
        url = "http://localhost:8080/hubs"
)
public interface HubController {
    //    업체의 소속 허브 조회
    @GetMapping(value = "/companies/{id}")
    ResponseEntity<ResponseDto<CompanyDto>> getHubByCompanyId(@PathVariable UUID id);

    //        상품의 소속 허브 조회
    @GetMapping(value = "/products/{id}")
    ResponseEntity<ResponseDto<ProductDto>> getHubByProductId(@PathVariable UUID id);


}
