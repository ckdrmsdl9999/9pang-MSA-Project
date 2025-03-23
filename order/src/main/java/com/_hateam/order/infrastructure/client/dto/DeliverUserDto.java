package com._hateam.order.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliverUserDto {
    private UUID deliverId;
    private Long userId;
    private String name;
    private String slackId;
    private String deliverType;
    private String contactNumber;
}