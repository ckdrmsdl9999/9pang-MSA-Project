package com._hateam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubRouteRequestDto {

    @NotNull(message = "허브 경로 ID는 필수입니다.")
    private UUID id;

    @NotNull(message = "출발지 허브 정보는 필수입니다.")
    private UUID sourceHubId;

    @NotNull(message = "도착지 허브 정보는 필수입니다.")
    private UUID destinationHubId;

    // 선택적 필드: 거리(킬로미터)와 예상 소요 시간(분)
    private Long distanceKm;
    private Integer estimatedTimeMinutes;

    // Redis에 저장되는 정보 추가
    private Double penaltyDistance; // 패널티 거리
    private List<Double> nodeDistances; // 노드간 개별 거리
    private List<String> route; // 경로
    private Double actualDistance; // 실제 거리
    private Double totalCost; // 총 비용
    private List<Double> cumulativeDistances; // 누적 거리
}
