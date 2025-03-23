package com._hateam.user.infrastructure.feign;

import com._hateam.common.dto.ResponseDto;
import com._hateam.user.application.dto.FeignHubDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name="hubs-service",url="${services.hub.url}")
public interface HubClient {

    @GetMapping("/hubs/{id}")
    ResponseDto<FeignHubDto> getHub(@PathVariable("id") UUID id);

}
