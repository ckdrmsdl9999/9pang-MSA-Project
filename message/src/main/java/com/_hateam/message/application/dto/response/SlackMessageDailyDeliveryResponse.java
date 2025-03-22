package com._hateam.message.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackMessageDailyDeliveryResponse {
    // TODO: 임의로 설정한 필드이므로 수정하기
    private String batchId;
    private int staffCount;
    private int sentMessages;
    private int failedMessages;
    private String executionTime;
}