package com._hateam.order.infrastructure.client;

import com._hateam.common.dto.ResponseDto;
import com._hateam.order.infrastructure.client.dto.SlackMessageRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "message-service", url = "${services.message.url}")
public interface MessageClient {

    @PostMapping("/api/slack/messages")
    ResponseDto<?> sendSlackMessage(@RequestBody SlackMessageRequestDto requestDto);
}