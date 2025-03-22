package com._hateam.message.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackMessageSearchRequest {
    private String receiverId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String keyword;
}