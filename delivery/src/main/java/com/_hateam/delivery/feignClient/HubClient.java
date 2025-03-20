package com._hateam.delivery.feignClient;

import com._hateam.delivery.dto.response.HubClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service", url = "${hub-service.url}")
public interface HubClient {

    @GetMapping("/api/hubRoutes/{id}")
    HubClientResponseDto getHubRoutesById(@PathVariable("id") Long id);
}

