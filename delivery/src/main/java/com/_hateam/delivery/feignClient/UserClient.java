package com._hateam.delivery.feignClient;

import com._hateam.common.dto.ResponseDto;
import com._hateam.delivery.dto.response.UserClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user")
public interface UserClient {

    @GetMapping("/api/users/username/{username}")
    ResponseEntity<ResponseDto<UserClientResponseDto>> getUserByUsername(@PathVariable("username") String username);

    // todo: 반환형태 확인후
    @GetMapping("/api/users/sequence/hub")
    ResponseEntity<ResponseDto<UserClientResponseDto>> getHubDeliver();

    @GetMapping("/api/users/sequence/company")
    ResponseEntity<ResponseDto<UserClientResponseDto>> getCompanyDeliver();


}

