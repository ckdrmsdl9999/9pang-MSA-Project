package com._hateam.entity;

import com._hateam.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_hub_route")
public class HubRoute extends Timestamped implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hub_route_id", nullable = false)
    private UUID id;

    // 출발지 허브와의 연관관계 (mandatory)
    @ManyToOne(optional = false)
    @JoinColumn(name = "source_hub_id", nullable = false)
    private Hub sourceHub;

    // 도착지 허브와의 연관관계 (mandatory)
    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_hub_id", nullable = false)
    private Hub destinationHub;

    @Column(name = "distance_km")
    private Long distanceKm;

    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;

    // Redis 저장 정보 추가
    @Column(name = "penalty_distance")
    private Double penaltyDistance;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "hub_route_node_distances", joinColumns = @JoinColumn(name = "hub_route_id"))
    @Column(name = "node_distance")
    private List<Double> nodeDistances;

    @ElementCollection
    @CollectionTable(name = "hub_route_path", joinColumns = @JoinColumn(name = "hub_route_id"))
    @Column(name = "route_node")
    private List<String> route;

    @Column(name = "actual_distance")
    private Double actualDistance;

    @Column(name = "total_cost")
    private Double totalCost;

    @ElementCollection
    @CollectionTable(name = "hub_route_cumulative_distances", joinColumns = @JoinColumn(name = "hub_route_id"))
    @Column(name = "cumulative_distance")
    private List<Double> cumulativeDistances;
}