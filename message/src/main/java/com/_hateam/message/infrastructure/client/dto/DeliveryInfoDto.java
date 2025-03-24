package com._hateam.message.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfoDto {
    private UUID deliveryId;
    private UUID companyId;
    private String companyName;
    private String companyAddress;
    private String receiverName;
}