package com._hateam.order.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.order.infrastructure.client.dto.DeliveryDto;
import com._hateam.order.infrastructure.client.dto.DeliveryStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "delivery-service", url = "${services.delivery.url}")
public interface DeliveryClient {

    @GetMapping("/api/deliveries/{deliveryId}")
    ResponseDto<DeliveryDto> getDelivery(@PathVariable("deliveryId") UUID deliveryId);

    @PutMapping("/api/deliveries/{deliveryId}/status")
    ResponseDto<Void> updateDeliveryStatus(
            @PathVariable("deliveryId") UUID deliveryId,
            @RequestBody DeliveryStatusDto statusDto);

    @DeleteMapping("/api/deliveries/{deliveryId}")
    ResponseDto<Void> deleteDelivery(@PathVariable("deliveryId") UUID deliveryId);
}