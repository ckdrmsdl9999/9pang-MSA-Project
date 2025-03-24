// order/src/main/java/com/_hateam/order/infrastructure/client/UserClient.java
package com._hateam.order.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.order.infrastructure.client.dto.DeliverUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${services.user.url}")
public interface UserClient {
    @GetMapping("/api/delivery-users/{deliverId}/slack")
    ResponseDto<DeliverUserDto> getDeliverUserById(@PathVariable("deliverId") UUID deliverId);
}