package com._hateam.dto;

import lombok.Data;

@Data
public class HubRouteResponseDto {
    private HubDto sourceHub;
    private HubDto destinationHub;
    private Long distanceKm;
    private Integer estimatedTimeMinutes;
}
