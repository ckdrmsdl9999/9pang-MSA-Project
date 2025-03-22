package com._hateam.message.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.message.infrastructure.client.dto.HubDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service", url = "${services.hub.url}")
public interface HubClient {
    @GetMapping("/hubs/{id}")
    ResponseDto<HubDto> getHub(@PathVariable("id") UUID id);
}