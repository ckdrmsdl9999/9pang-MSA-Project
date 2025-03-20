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
public class DeliveryDto {
    private UUID id;
    private UUID orderId;
    private String status;
    private UUID startHubId;
    private UUID endHubId;
    private String receiverAddress;
    private String receiverName;
    private String receiverSlackId;
    private UUID delivererId;
}