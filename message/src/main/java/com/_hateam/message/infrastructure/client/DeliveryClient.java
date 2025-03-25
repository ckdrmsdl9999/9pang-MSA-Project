package com._hateam.message.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.message.infrastructure.client.dto.DeliveryInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "delivery-service", url = "${services.delivery.url}")
public interface DeliveryClient {
    @GetMapping("/api/deliveries/daily")
    ResponseDto<List<DeliveryInfoDto>> getDailyDeliveries(
            @RequestParam("delivererId") UUID delivererId,
            @RequestParam("hubId") UUID hubId);
}