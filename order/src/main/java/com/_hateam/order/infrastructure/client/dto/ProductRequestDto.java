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
public class ProductRequestDto {
    private String name;
    private Integer quantity;
    private String description;
    private Integer price;
    private Boolean isAvailable;
    private UUID companyId;
    private UUID hubId;
}