package com._hateam.message.event;

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
public class DeliveryStatusChangedEvent {
    private UUID deliveryId;

    private UUID orderId;

    private String newStatus;

    private LocalDateTime statusChangedAt;
}