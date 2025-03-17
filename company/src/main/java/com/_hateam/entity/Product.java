package com._hateam.entity;

import com._hateam.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_hub_route")
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hub_route_id", nullable = false)
    private UUID id;

    // 출발지 허브와의 연관관계 (mandatory)
    @ManyToOne(optional = false)
    @JoinColumn(name = "source_hub_id", nullable = false)
    private Company sourceCompany;

    // 도착지 허브와의 연관관계 (mandatory)
    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_hub_id", nullable = false)
    private Company destinationCompany;

    @Column(name = "distance_km")
    private Long distanceKm;

    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;
}
