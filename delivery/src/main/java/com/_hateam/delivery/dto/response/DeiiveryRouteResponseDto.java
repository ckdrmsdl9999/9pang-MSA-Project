package com._hateam.delivery.dto.response;

import com._hateam.delivery.entity.DeliveryRoute;
import com._hateam.delivery.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeiiveryRouteResponseDto {
    private UUID id;
    private UUID deliveryId;
    private DeliveryStatus status;
    private Integer sequence;
    private UUID startHubId;
    private UUID endHubId;
    private Long predicDistance;
    private Integer predicTime;
    private Long realDistance;
    private Integer realTime;
    private UUID deliverId;

    @Builder
    public DeiiveryRouteResponseDto(UUID id, UUID deliveryId,
                                    DeliveryStatus status, Integer sequence,
                                    UUID startHubId, UUID endHubId,
                                    Long predicDistance, Integer predicTime,
                                    Long realDistance, Integer realTime,
                                    UUID deliverId
    ) {
        this.id = id;
        this.deliveryId = deliveryId;
        this.status = status;
        this.sequence = sequence;
        this.startHubId = startHubId;
        this.endHubId = endHubId;
        this.predicDistance = predicDistance;
        this.predicTime = predicTime;
        this.realDistance = realDistance;
        this.realTime = realTime;
        this.deliverId = deliverId;
    }


    /**
     * todo: mapper로 분리?
     */
    public static DeiiveryRouteResponseDto from(DeliveryRoute deliveryRoute) {

        return DeiiveryRouteResponseDto.builder()
                .id(deliveryRoute.getId())
                .deliveryId(deliveryRoute.getDelivery().getId())
                .status(deliveryRoute.getStatus())
                .sequence(deliveryRoute.getSequence())
                .startHubId(deliveryRoute.getStartHubId())
                .endHubId(deliveryRoute.getEndHubId())
                .predicDistance(deliveryRoute.getPredicDistance())
                .predicTime(deliveryRoute.getPredicTime())
                .realDistance(deliveryRoute.getRealDistance())
                .realTime(deliveryRoute.getRealTime())
                .deliverId(deliveryRoute.getDeliverId())
                .build();
    }
}
