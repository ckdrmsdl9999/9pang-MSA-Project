package com._hateam.feign;

import com._hateam.common.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;
@FeignClient(
        name = "ProductController",
        url = "http://localhost:8080/companies/products"
)
public interface ProductController {
    //    허브의 업체 조회

    @GetMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDto<ProductDto>> getProduct(@PathVariable UUID id);
}
