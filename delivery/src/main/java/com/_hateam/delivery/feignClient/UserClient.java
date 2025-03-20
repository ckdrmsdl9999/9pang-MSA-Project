package com._hateam.delivery.feignClient;

import com._hateam.delivery.dto.response.UserClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

    @GetMapping("api/users/{id}")
    UserClientResponseDto getUserById(@PathVariable("id") Long id);
}

