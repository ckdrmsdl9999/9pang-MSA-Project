package com._hateam.message.infrastructure.client;

import com._hateam.message.infrastructure.client.dto.NaverDirectionsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naver-directions", url = "${naver.api.directions.url}")
public interface NaverDirectionsApi {

    @GetMapping("/v1/driving")
    NaverDirectionsResponse getDirections(
            @RequestHeader("X-NCP-APIGW-API-KEY-ID") String apiKeyId,
            @RequestHeader("X-NCP-APIGW-API-KEY") String apiKey,
            @RequestParam("start") String start,
            @RequestParam("goal") String goal,
            @RequestParam(value = "waypoints", required = false) String waypoints,
            @RequestParam(value = "option", required = false) String option
    );
}