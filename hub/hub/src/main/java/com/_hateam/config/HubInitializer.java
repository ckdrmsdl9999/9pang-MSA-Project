package com._hateam.config;

import com._hateam.feign.aistudio.feign.GeminiClient;
import com._hateam.dto.HubResponseDto;
import com._hateam.entity.Hub;
import com._hateam.entity.HubRoute;
import com._hateam.repository.HubRepository;
import com._hateam.repository.HubRouteRepository;
import com._hateam.service.GraphPathFinder;
import com._hateam.service.HubGraph;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubInitializer {

    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteRepository;
    private final GeminiClient geminiClient; // Feign Client 주입
    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입

    @PostConstruct
    @Transactional
    public void initializeHubs() {
        // 허브가 이미 존재하면 초기화를 건너뛰도록 할 수도 있음
//        if (hubRepository.count() > 0) return;

        // 정적 허브 데이터 목록 (이름은 HubGraph에 정의된 노드와 일치하도록)
        List<HubResponseDto> hubDataList = Arrays.asList(
                new HubResponseDto("서울", "서울특별시 송파구 송파대로 55", "37.4562557", "126.7052062"),
                new HubResponseDto("경기 북부", "경기도 고양시 덕양구 권율대로 570", "37.632348", "126.852348"),
                new HubResponseDto("경기 남부", "경기도 이천시 덕평로 257-21", "37.4562557", "126.7052062"),
                new HubResponseDto("부산", "부산 동구 중앙대로 206", "35.1141634", "129.0345914"),
                new HubResponseDto("대구", "대구 북구 태평로 161", "35.8828351", "128.5996849"),
                new HubResponseDto("인천", "인천 남동구 정각로 29", "37.4475431", "126.7052062"),
                new HubResponseDto("광주", "광주 서구 내방로 111", "35.1595454", "126.852348"),
                new HubResponseDto("대전", "대전 서구 둔산로 100", "36.3504119", "127.3848135"),
                new HubResponseDto("울산", "울산 남구 중앙로 201", "35.538564", "129.311359"),
                new HubResponseDto("세종", "세종특별자치시 한누리대로 2130", "36.4801124", "127.2890587"),
                new HubResponseDto("강원도", "강원특별자치도 춘천시 중앙로 1", "37.8812616", "127.7291884"),
                new HubResponseDto("충청 북도", "충북 청주시 상당구 상당로 82", "36.6353242", "127.4897855"),
                new HubResponseDto("충청 남도", "충남 홍성군 홍북읍 충남대로 21", "36.6025547", "126.6667362"),
                new HubResponseDto("전라 북도", "전북특별자치도 전주시 완산구 효자로 225", "35.8203627", "127.1068278"),
                new HubResponseDto("전라 남도", "전남 무안군 삼향읍 오룡길 1", "34.8492021", "126.4715102"),
                new HubResponseDto("경상 북도", "경북 안동시 풍천면 도청대로 455", "36.578652", "128.425883"),
                new HubResponseDto("경상 남도", "경남 창원시 의창구 중앙대로 300", "35.2343864", "128.6925514")
        );

        // 허브 저장 및 이름 기준 맵 구성
        Map<String, Hub> hubMap = new HashMap<>();
        for (HubResponseDto data : hubDataList) {
            Hub hub = saveHub(data.getName(), data.getAddress(), data.getLatitude(), data.getLongitude());
            hubMap.put(data.getName(), hub);
            log.info("Saved Hub: {}", hub);
        }

        // HubGraph 객체를 생성하여 허브 연결 구조(간선 정보)를 가져옴
        HubGraph hubGraph = new HubGraph();
        Map<String, List<String>> graphStructure = hubGraph.getGraph();
        GraphPathFinder pathFinder = new GraphPathFinder(hubGraph, hubMap);

        // HubGraph에 정의된 연결 정보를 기반으로, DB에 HubRoute 엔티티를 저장 (중복 방지를 위해, 양방향 간선은 한 번만 저장)
        Collator collator = Collator.getInstance(Locale.KOREAN);
        for (String sourceName : graphStructure.keySet()) {
            for (String neighbor : graphStructure.get(sourceName)) {
                // sourceName이 neighbor보다 사전순으로 앞서면 HubRoute 엔티티를 저장(중복 방지)
                if (collator.compare(sourceName, neighbor) < 0) {
                    Hub source = hubMap.get(sourceName);
                    Hub dest = hubMap.get(neighbor);
                    if (source != null && dest != null) {
                        try {
                            double lat1 = Double.parseDouble(source.getLatitude());
                            double lon1 = Double.parseDouble(source.getLongitude());
                            double lat2 = Double.parseDouble(dest.getLatitude());
                            double lon2 = Double.parseDouble(dest.getLongitude());
                            double distance = pathFinder.haversine(lat1, lon1, lat2, lon2);
                            saveHubRoute(source, dest, distance);
                            log.info("Saved HubRoute: {} -> {} with distance {} km", sourceName, neighbor, distance);
                        } catch (NumberFormatException e) {
                            log.warn("Invalid coordinate format for hub route {} -> {}", sourceName, neighbor);
                        }
                    }
                }
            }
        }



        // 각 노드 쌍에 대해, 수정된 다익스트라 알고리즘을 사용하여 최단 경로 비용 계산 후 Redis에 저장
        // DB에 HubRoute 엔티티를 저장 (중복 방지를 위해, 양방향 간선은 한 번만 저장)
        List<String> hubNames = new ArrayList<>(hubMap.keySet());
        for (String start : hubNames) {
            for (String end : hubNames) {
                if (!start.equals(end)) {
                    GraphPathFinder.PathResult result = pathFinder.findMinimumCostPath(start, end);
                    if (result != null) {
                        // 최종 결과: 총 비용과 경로 리스트를 함께 저장 (예: JSON 형식의 Map으로 저장)
                        Map<String, Object> routeResult = new HashMap<>();
                        routeResult.put("actualDistance", result.getActualDistance());
                        routeResult.put("penaltyDistance", result.getPenalty());
                        routeResult.put("totalCost", result.getTotalCost());
                        routeResult.put("path", result.getPath());
                        String key = "route:" + start + ":" + end;
                        redisTemplate.opsForValue().set(key, routeResult);
                        log.info("Route from {} to {}: {} km, Path: {}", start, end, result.getTotalCost(), result.getPath());
                    } else {
                        log.warn("No path found from {} to {}", start, end);
                    }
                }
            }
        }

        // 직접 연결된 간선의 기본 Haversine 거리를 Redis에 저장 (캐시용)
        Map<String, Double> directEdges = new HashMap<>();
        for (String sourceName : graphStructure.keySet()) {
            for (String neighbor : graphStructure.get(sourceName)) {
                Hub source = hubMap.get(sourceName);
                Hub dest = hubMap.get(neighbor);
                if (source != null && dest != null) {
                    try {
                        double lat1 = Double.parseDouble(source.getLatitude());
                        double lon1 = Double.parseDouble(source.getLongitude());
                        double lat2 = Double.parseDouble(dest.getLatitude());
                        double lon2 = Double.parseDouble(dest.getLongitude());
                        double distance = pathFinder.haversine(lat1, lon1, lat2, lon2);
                        directEdges.put(sourceName + ":" + neighbor, distance);
                        log.info("Direct edge {} -> {}: {} km", sourceName, neighbor, distance);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid coordinate format for direct edge {} -> {}", sourceName, neighbor);
                    }
                }
            }
        }
        redisTemplate.opsForHash().putAll("hubGraphDirect", directEdges);
        log.info("Direct hub graph saved to Redis.");
    }


    @CachePut(value = "hub", key = "#result.id")
    public Hub saveHub(String name, String address, String latitude, String longitude) {
        Hub hub = Hub.builder()
                .name(name)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        return hubRepository.save(hub);
    }


    private void saveHubRoute(Hub source, Hub dest, double distance) {
        // estimatedTimeMinutes는 여기서는 기본값 0으로 설정 (필요에 따라 계산 가능)
        HubRoute route = HubRoute.builder()
                .sourceHub(source)
                .destinationHub(dest)
                .distanceKm((long) Math.round(distance))
                .estimatedTimeMinutes(0)
                .build();
        hubRouteRepository.save(route);
    }

}
