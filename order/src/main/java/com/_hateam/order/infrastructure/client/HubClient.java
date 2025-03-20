package com._hateam.order.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.order.infrastructure.client.dto.HubDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service", url = "${services.hub.url}")
public interface HubClient {

    @GetMapping("/hubs/{hubId}")
    ResponseDto<HubDto> getHubById(@PathVariable("hubId") UUID hubId);
}