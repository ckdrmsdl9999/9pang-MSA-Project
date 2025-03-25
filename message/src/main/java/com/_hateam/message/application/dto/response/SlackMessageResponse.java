package com._hateam.message.application.dto.response;

import com._hateam.message.domain.model.SlackMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackMessageResponse {
    private UUID messageId;
    private String receiverId;
    private String content;
    private LocalDateTime sentAt;
    private String status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;

    public static SlackMessageResponse from(SlackMessage message) {
        return SlackMessageResponse.builder()
                .messageId(message.getMessageId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .status(message.getStatus() != null ? message.getStatus().name() : null)
                .createdAt(message.getCreatedAt())
                .createdBy(message.getCreatedBy())
                .updatedAt(message.getUpdatedAt())
                .updatedBy(message.getUpdatedBy())
                .deletedAt(message.getDeletedAt())
                .deletedBy(message.getDeletedBy())
                .build();
    }
}