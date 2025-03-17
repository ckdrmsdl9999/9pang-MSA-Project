package com._hateam.dto;

import com._hateam.entity.HubRoute;
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

    // HubRoute 엔티티를 DTO로 변환하는 정적 팩토리 메소드
    public static HubRouteDto fromEntity(HubRoute hubRoute) {
        return HubRouteDto.builder()
                .id(hubRoute.getId())
                .sourceHub(HubDto.hubToHubDto(hubRoute.getSourceHub()))
                .destinationHub(HubDto.hubToHubDto(hubRoute.getDestinationHub()))
                .distanceKm(hubRoute.getDistanceKm())
                .estimatedTimeMinutes(hubRoute.getEstimatedTimeMinutes())
                .build();
    }
}
