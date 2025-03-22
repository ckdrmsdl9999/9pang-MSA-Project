package com._hateam.message.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackMessageDailyDeliveryRequest {
    private String sendTime;

    private UUID hubId;
}