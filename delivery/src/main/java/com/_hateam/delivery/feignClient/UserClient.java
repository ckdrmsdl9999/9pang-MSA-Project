package com._hateam.delivery.feignClient;

import com._hateam.common.dto.ResponseDto;
import com._hateam.delivery.dto.response.UserClientDeliverResponseDto;
import com._hateam.delivery.dto.response.UserClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "user")
public interface UserClient {

    @GetMapping("/api/users/username/{username}")
    ResponseEntity<ResponseDto<UserClientResponseDto>> getUserByUsername(@PathVariable("username") String username);

    @GetMapping("/api/deliver-assign")
    ResponseEntity<ResponseDto<UserClientDeliverResponseDto>> getDeliverAssign(@RequestParam String deliverType, UUID hubId);
}

