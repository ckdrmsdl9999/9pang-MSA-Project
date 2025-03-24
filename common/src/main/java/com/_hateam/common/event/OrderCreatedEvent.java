package com._hateam.common.event;

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
public class OrderCreatedEvent implements KafkaEvent {
    private UUID orderId;

    private UUID deliveryId;

    private UUID hubId;

    private UUID companyId;

    private String orderRequest;

    private LocalDateTime deliveryDeadline;

    private String receiverAddress;

    private String receiverName;

    private String receiverSlackId;
}