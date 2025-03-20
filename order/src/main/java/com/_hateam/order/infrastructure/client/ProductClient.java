package com._hateam.order.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.order.infrastructure.client.dto.ProductDto;
import com._hateam.order.infrastructure.client.dto.ProductRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "product-service", url = "${services.product.url}")
public interface ProductClient {

    @GetMapping("/companies/products/{productId}")
    ResponseDto<ProductDto> getProductById(@PathVariable("productId") UUID productId);

    @PatchMapping("/companies/products/{productId}")
    ResponseDto<ProductDto> updateProduct(
            @PathVariable("productId") UUID productId,
            @RequestBody ProductRequestDto requestDto);
}