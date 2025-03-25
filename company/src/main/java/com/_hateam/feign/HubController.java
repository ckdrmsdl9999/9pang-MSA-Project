package com._hateam.feign;

import com._hateam.common.dto.ResponseDto;
import com._hateam.dto.HubDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "hubs-service",
        url = "http://localhost:8080/hubs"
)
public interface HubController {
    // 업체의 소속 허브 조회
    @GetMapping(value = "/companies/{id}")
    ResponseEntity<ResponseDto<HubDto>> getHubByCompanyId(@PathVariable UUID id);

    // 상품의 소속 허브 조회
    @GetMapping(value = "/products/{id}")
    ResponseEntity<ResponseDto<HubDto>> getHubByProductId(@PathVariable UUID id);

    @GetMapping(value = "/{id}")
    ResponseEntity<ResponseDto<HubDto>> getHub(@PathVariable UUID id);
}
