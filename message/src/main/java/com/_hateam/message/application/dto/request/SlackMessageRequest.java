package com._hateam.message.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackMessageRequest {
    @NotBlank(message = "수신자 ID는 필수입니다.")
    private String receiverId;

    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String content;
}