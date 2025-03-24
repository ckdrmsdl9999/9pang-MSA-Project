package com._hateam.message.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.message.infrastructure.client.dto.DeliverUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {s

      @GetMapping("/api/delivery-users/{deliverId}/slack")
      ResponseDto<DeliverUserDto> getDeliverUserById(@PathVariable("deliverId") UUID deliverId);

      @GetMapping("/api/delivery-users/delivery")
      ResponseDto<List<DeliverUserDto>> getCompanyDeliverers();
}