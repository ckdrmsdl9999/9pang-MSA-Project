package com._hateam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotNull(message = "출발지 허브 ID는 필수입니다.")
    private UUID sourceHubId;

    @NotNull(message = "도착지 허브 ID는 필수입니다.")
    private UUID destinationHubId;

    // 선택적 필드
    private Long distanceKm;
    private Integer estimatedTimeMinutes;
}
