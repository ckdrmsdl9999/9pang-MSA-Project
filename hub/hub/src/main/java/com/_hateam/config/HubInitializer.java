package com._hateam.config;

import com._hateam.entity.Hub;
import com._hateam.entity.HubRoute;
import com._hateam.repository.HubRepository;
import com._hateam.repository.HubRouteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
@RequiredArgsConstructor
public class HubInitializer {

    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteRepository;

    @PostConstruct
    @Transactional
    public void initHubsAndRoutes() {
        if (hubRepository.count() > 0) return;

        // 허브 생성 (공식 영어 이름 사용)
        Hub gyeonggiSouth = saveHub("Gyeonggi-do (South)");
        Hub gyeonggiNorth = saveHub("Gyeonggi-do (North)");
        Hub seoul = saveHub("Seoul");
        Hub incheon = saveHub("Incheon");
        Hub gangwondo = saveHub("Gangwon-do");
        Hub northGyeongsang = saveHub("North Gyeongsang Province");
        Hub daejeon = saveHub("Daejeon");
        Hub daegu = saveHub("Daegu");
        Hub chungcheongnam = saveHub("Chungcheongnam-do");
        Hub chungcheongbuk = saveHub("Chungcheongbuk-do");
        Hub sejong = saveHub("Sejong");
        Hub jeollabuk = saveHub("Jeollabuk-do");
        Hub gwangju = saveHub("Gwangju");
        Hub jeollanam = saveHub("Jeollanam-do");
        Hub southGyeongsang = saveHub("South Gyeongsang Province");
        Hub busan = saveHub("Busan");
        Hub ulsan = saveHub("Ulsan");

        // 경로 추가

        // "Gyeonggi-do (South)" connects to "Gyeonggi-do (North), Seoul, Incheon, Gangwon-do, North Gyeongsang Province, Daejeon, Daegu"
        saveRoute(gyeonggiSouth, gyeonggiNorth, 50L, 60);
        saveRoute(gyeonggiSouth, seoul, 30L, 40);
        saveRoute(gyeonggiSouth, incheon, 40L, 50);
        saveRoute(gyeonggiSouth, gangwondo, 100L, 120);
        saveRoute(gyeonggiSouth, northGyeongsang, 200L, 180);
        saveRoute(gyeonggiSouth, daejeon, 150L, 140);
        saveRoute(gyeonggiSouth, daegu, 250L, 210);

        // "Daejeon" connects to "Chungcheongnam-do, Chungcheongbuk-do, Sejong, Jeollabuk-do, Gwangju, Jeollanam-do, Gyeonggi-do (South), Daegu"
        saveRoute(daejeon, chungcheongnam, 60L, 70);
        saveRoute(daejeon, chungcheongbuk, 70L, 80);
        saveRoute(daejeon, sejong, 20L, 30);
        saveRoute(daejeon, jeollabuk, 90L, 100);
        saveRoute(daejeon, gwangju, 140L, 150);
        saveRoute(daejeon, jeollanam, 130L, 140);  // 추가: Jeollanam-do
        saveRoute(daejeon, gyeonggiSouth, 150L, 140);
        saveRoute(daejeon, daegu, 170L, 160);

        // "Daegu" connects to "North Gyeongsang Province, South Gyeongsang Province, Busan, Ulsan, Gyeonggi-do (South), Daejeon"
        saveRoute(daegu, northGyeongsang, 50L, 60);
        saveRoute(daegu, southGyeongsang, 90L, 100);
        saveRoute(daegu, busan, 120L, 130);
        saveRoute(daegu, ulsan, 110L, 120);
        saveRoute(daegu, gyeonggiSouth, 250L, 210); // 만약 대구와 경기남부 양방향 연결이 필요하다면
        saveRoute(daegu, daejeon, 170L, 160);        // 이미 추가된 대전-대구 경로와 대칭 관계

        // "North Gyeongsang Province" connects to "Gyeonggi-do (South), Daegu"
        saveRoute(northGyeongsang, gyeonggiSouth, 200L, 180); // 이미 추가되었을 수 있으므로 중복 추가는 생략 가능
        saveRoute(northGyeongsang, daegu, 50L, 60); // 이미 추가됨
    }

    private Hub saveHub(String name) {
        return hubRepository.save(
                Hub.builder()
                        .name(name)
                        .address("N/A")
                        .latitude("0")
                        .longitude("0")
                        .build()
        );
    }

    private void saveRoute(Hub from, Hub to, Long distance, Integer time) {
        hubRouteRepository.save(
                HubRoute.builder()
                        .sourceHub(from)
                        .destinationHub(to)
                        .distanceKm(distance)
                        .estimatedTimeMinutes(time)
                        .build()
        );
    }
}
