package com._hateam.entity;

import com._hateam.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_hub")
public class Hub extends Timestamped implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hub_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "latitude", nullable = false, length = 20)
    private String latitude;

    @Column(name = "longitude", nullable = false, length = 20)
    private String longitude;

    // 해당 Hub가 출발지로 사용된 경로들 (선택적)
    @OneToMany(mappedBy = "sourceHub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HubRoute> outboundRoutes = new ArrayList<>();

    // 해당 Hub가 도착지로 사용된 경로들 (선택적)
    @OneToMany(mappedBy = "destinationHub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HubRoute> inboundRoutes = new ArrayList<>();

    /**
     * 출발지 경로 추가 (양방향 연관관계 설정)
     */
    public void addOutboundRoute(HubRoute hubRoute) {
        outboundRoutes.add(hubRoute);
        hubRoute.setSourceHub(this);
    }

    /**
     * 도착지 경로 추가 (양방향 연관관계 설정)
     */
    public void addInboundRoute(HubRoute hubRoute) {
        inboundRoutes.add(hubRoute);
        hubRoute.setDestinationHub(this);
    }

    /**
     * 경로 제거 (출발지 기준)
     */
    public void removeOutboundRoute(HubRoute hubRoute) {
        outboundRoutes.remove(hubRoute);
        hubRoute.setSourceHub(null);
    }

    /**
     * 경로 제거 (도착지 기준)
     */
    public void removeInboundRoute(HubRoute hubRoute) {
        inboundRoutes.remove(hubRoute);
        hubRoute.setDestinationHub(null);
    }
}
