package com._hateam.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedForSlackEvent {
    private UUID orderId;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private String productInfo;
    private String requestInfo;
    private String startHub;
    private List<String> viaHubs;
    private String destination;
    private String delivererName;
    private String delivererSlackId;
    private LocalDateTime deliveryDeadline;
}