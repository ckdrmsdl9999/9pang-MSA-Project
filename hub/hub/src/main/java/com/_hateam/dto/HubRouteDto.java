package com._hateam.dto;

import com._hateam.entity.HubRoute;
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
public class HubRouteDto {

    @NotNull(message = "허브 경로 ID는 필수입니다.")
    private UUID id;

    @NotNull(message = "출발지 허브 정보는 필수입니다.")
    private HubDto sourceHub;

    @NotNull(message = "도착지 허브 정보는 필수입니다.")
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

    // HubRoute 엔티티를 DTO로 변환하는 정적 팩토리 메소드
    public static HubRouteDto fromEntity(HubRoute hubRoute) {
        return HubRouteDto.builder()
                .id(hubRoute.getId())
                .sourceHub(HubDto.hubToHubDto(hubRoute.getSourceHub()))
                .destinationHub(HubDto.hubToHubDto(hubRoute.getDestinationHub()))
                .distanceKm(hubRoute.getDistanceKm())
                .estimatedTimeMinutes(hubRoute.getEstimatedTimeMinutes())
                // Redis 저장 정보 추가
                .penaltyDistance(hubRoute.getPenaltyDistance())
                .nodeDistances(hubRoute.getNodeDistances())
                .route(hubRoute.getRoute())
                .actualDistance(hubRoute.getActualDistance())
                .totalCost(hubRoute.getTotalCost())
                .cumulativeDistances(hubRoute.getCumulativeDistances())
                .build();
    }
}