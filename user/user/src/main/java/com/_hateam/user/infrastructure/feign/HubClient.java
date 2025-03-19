package com._hateam.user.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name="hub")
public interface HubClient {

    @GetMapping("/hubs/{id}")
    ResponseDto<HubDto> getHub(@PathVariable("id") UUID id);

}
