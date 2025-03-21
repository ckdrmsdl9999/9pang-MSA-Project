package com._hateam.delivery.entity;

import com._hateam.common.entity.Timestamped;
import com._hateam.delivery.dto.request.UpdateDeliveryRequestDto;
import com._hateam.delivery.dto.response.CompanyClientResponseDto;
import com._hateam.delivery.dto.response.OrderClientResponseDto;
import com._hateam.delivery.dto.response.UserClientDeliverResponseDto;
import com._hateam.delivery.dto.response.UserClientResponseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "p_delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends Timestamped {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false, unique = true)
    private UUID startHubId;

    @Column(nullable = false, unique = true)
    private UUID endHubId;

    @Column(nullable = false)
    private String receiverAddress;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverSlackId;

    // nullable
    private UUID deliverId;
    private String deliverSlackId;

    @OneToMany(fetch = LAZY, cascade = PERSIST)
    private List<DeliveryRoute> deliveryRouteList;


    @Builder
    private Delivery(final UUID orderId,
                     final DeliveryStatus status,
                     final UUID startHubId,
                     final UUID endHubId,
                     final String receiverAddress,
                     final String receiverName,
                     final String receiverSlackId) {
        this.orderId = orderId;
        this.status = status;
        this.startHubId = startHubId;
        this.endHubId = endHubId;
        this.receiverAddress = receiverAddress;
        this.receiverName = receiverName;
        this.receiverSlackId = receiverSlackId;
    }

    /**
     * todo: mapper로 분리,
     */
    public static Delivery addOf(final OrderClientResponseDto orderClientResponseDto,
                                 final CompanyClientResponseDto companyClientResponseDto,
                                 final UserClientResponseDto userClientResponseDto,
                                 final UUID destHubId) {
        return Delivery.builder()
                .orderId(orderClientResponseDto.getOrderId())
                .status(DeliveryStatus.WAITING_AT_HUB)
                .startHubId(orderClientResponseDto.getHubId())
                .endHubId(destHubId) // 현재 랜덤 uuid를 넣도록 함
                .receiverAddress(companyClientResponseDto.getCompanyAddress())
                .receiverName(companyClientResponseDto.getUsername())
                .receiverSlackId(userClientResponseDto.getSlackId())
                .build();
    }

    public void addDeliveyRouteListFrom(List<DeliveryRoute> deliveryRouteList) {
        this.deliveryRouteList = deliveryRouteList;
    }

    public void updateOf(UpdateDeliveryRequestDto requestDto) {
        this.orderId = requestDto.getOrderId();
        this.status = requestDto.getStatus();
        this.startHubId = requestDto.getStartHubId();
        this.endHubId = requestDto.getEndHubId();
        this.receiverAddress = requestDto.getReceiverAddress();
        this.receiverName = requestDto.getReceiverName();
        this.receiverSlackId = requestDto.getReceiverSlackId();
        this.deliverId = requestDto.getDelivererId();
    }

    public void updateStatusOf(DeliveryStatus status) {
        this.status = status;
    }

    public void updateDeliver(UserClientDeliverResponseDto responseDto) {
        this.deliverId = responseDto.getDeliverId();
        this.deliverSlackId = responseDto.getSlackId();
    }

    public void deleteOf(final String deletedBy) {
        super.delete(deletedBy);
    }
}