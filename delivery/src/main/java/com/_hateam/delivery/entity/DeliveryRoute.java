package com._hateam.delivery.entity;

import com._hateam.common.entity.Timestamped;
import com._hateam.delivery.dto.request.UpdateDeliveryRouteRequestDto;
import com._hateam.delivery.dto.response.HubClientResponseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_delivery_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryRoute extends Timestamped {

    @Id
    @UuidGenerator
    private UUID id; // todo: id명 확인 필요

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private Integer sequence; // todo: 어떤형태를 사용할지 고민 추가 필요

    @Column(nullable = false, unique = true)
    private UUID startHubId;

    @Column(nullable = false, unique = true)
    private UUID endHubId;

    @Column(nullable = false)
    private Long predicDistance;

    @Column(nullable = false)
    private Integer predicTime;

    // nullable
    private Long realDistance;

    // nullable
    private Integer realTime;

    private UUID delivererId;


    @Builder
    private DeliveryRoute(final Delivery delivery,
                     final DeliveryStatus status,
                     final Integer sequence,
                     final UUID startHubId,
                     final UUID endHubId,
                     final Long predicDistance,
                     final Integer predicTime) {
        this.delivery = delivery;
        this.status = status;
        this.sequence = sequence;
        this.startHubId = startHubId;
        this.endHubId = endHubId;
        this.predicDistance = predicDistance;
        this.predicTime = predicTime;
    }

    /**
     * todo: mapper로 분리,
     */
    public static DeliveryRoute addOf(final Delivery delivery,
                                      final UUID startHubId,
                                      final UUID endHubId,
                                      final HubClientResponseDto hubClientResponseDto
                                      ) {
        return DeliveryRoute.builder()
                .delivery(delivery)
                .status(DeliveryStatus.WAITING_AT_HUB)
                .sequence(hubClientResponseDto.getSequence())
                .startHubId(startHubId)
                .endHubId(endHubId)
                .predicDistance(hubClientResponseDto.getDistanceKm())
                .predicTime(hubClientResponseDto.getEstimatedTimeMinutes())
                .build();
    }

    public void updateStatusOf(UpdateDeliveryRouteRequestDto requestDto) {
        this.status = requestDto.getStatus();
        this.realDistance = requestDto.getRealDistance();
        this.realTime = requestDto.getRealTime();
    }

    public void updateDelivererId(UUID delivererId) {
        this.delivererId = delivererId;
    }

    public void deleteOf(final String deletedBy) {
        super.delete(deletedBy);
    }
}
