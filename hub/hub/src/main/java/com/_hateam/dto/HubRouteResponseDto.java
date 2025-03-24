package com._hateam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class HubRouteResponseDto {
    private HubDto sourceHub;

    private HubDto destinationHub;

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
