package com._hateam.message.domain.model;

import com._hateam.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_company_delivery_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDeliveryRoute extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "route_id", columnDefinition = "uuid")
    private UUID routeId;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Column(name = "start_hub_id", nullable = false)
    private UUID startHubId;

    @Column(name = "destination_company_id", nullable = false)
    private UUID destinationCompanyId;

    @Column(name = "expected_distance_km")
    private Double expectedDistanceKm;

    @Column(name = "expected_time_minutes")
    private Integer expectedTimeMinutes;

    @Column(name = "actual_distance_km")
    private Double actualDistanceKm;

    @Column(name = "actual_time_minutes")
    private Integer actualTimeMinutes;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DeliveryRouteStatus status;

    @Column(name = "deliver_id", nullable = false)
    private UUID deliverId;

    @Column(name = "delivery_order")
    private Integer deliveryOrder;

    @Column(name = "destination_latitude")
    private String destinationLatitude;

    @Column(name = "destination_longitude")
    private String destinationLongitude;

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "destination_address")
    private String destinationAddress;

    public enum DeliveryRouteStatus {
        WAITING,
        MOVING_TO_COMPANY,
        DELIVERED
    }
}