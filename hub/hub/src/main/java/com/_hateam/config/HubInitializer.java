package com._hateam.config;

import com._hateam.aistudio.dto.GeminiRequestDto;
import com._hateam.aistudio.dto.GeminiResponseDto;
import com._hateam.aistudio.feign.GeminiClient;
import com._hateam.dto.HubResponseDto;
import com._hateam.entity.Hub;
import com._hateam.entity.HubRoute;
import com._hateam.repository.HubRepository;
import com._hateam.repository.HubRouteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubInitializer {

    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteRepository;
    private final GeminiClient geminiClient; // Feign Client 주입

    @PostConstruct
    @Transactional
    public void initializeHubsFromGemini() {
        if (hubRepository.count() > 0) return;

        // 17개 지역의 공식 영어 명칭 (Google Maps 기준)
        List<String> regions = Arrays.asList(
                "Gyeonggi-do (South)",
                "Gyeonggi-do (North)",
                "Seoul",
                "Incheon",
                "Gangwon-do",
                "North Gyeongsang Province",
                "Daejeon",
                "Daegu",
                "Chungcheongnam-do",
                "Chungcheongbuk-do",
                "Sejong",
                "Jeollabuk-do",
                "Gwangju",
                "Jeollanam-do",
                "South Gyeongsang Province",
                "Busan",
                "Ulsan"
        );

        for (String region : regions) {
            GeminiRequestDto requestDto = new GeminiRequestDto();
            // setPrompt 메소드 내부에서는 아래와 같이 프롬프트를 설정하도록 구현되어 있음:
            // "region 의 지도 정보가 필요해. 답변은 정확히 'name:ex1, address:ex2, latitude:ex3, longitude:ex4'의 형식으로만 답변해."
            requestDto.setPrompt(region);

            // Gemini API 호출
            GeminiResponseDto response = geminiClient.generateContent(requestDto);
            if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
                log.warn("No response received for region: {}", region);
                continue;
            }

            // 응답에서 텍스트 추출
            // 가정: 응답 텍스트 형식: "name:ex1, address:ex2, latitude:ex3, longitude:ex4"
            String responseText = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            log.info(responseText);
            String[] parts = responseText.split(",");
            if (parts.length < 4) {
                log.warn("Invalid response format for region: {}. Response: {}", region, responseText);
                continue;
            }
            try {
                String name = parts[0].split(":")[1].trim();
                String address = parts[1].split(":")[1].trim();
                String latitude = parts[2].split(":")[1].trim();
                String longitude = parts[3].split(":")[1].trim();

                // Hub 엔티티 생성 및 저장 (저장 시 캐시에 등록됨)
                Hub hub = saveHub(name, address, latitude, longitude);
                log.info("Hub created for region {}: {}", region, hub);
            } catch (Exception e) {
                log.warn("Error parsing response for region {}. Response: {}", region, responseText);
            }
            try {
                // 20초 대기 (각 API 호출 후 20초 휴식)
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Hub initializer is finished.");
    }
//
//        // 예시: 외부 데이터로부터 경로 정보도 제공된다면 처리할 수 있음.
//        // 만약 경로 정보가 별도의 API로 제공된다면 Feign Client를 추가로 사용하거나,
//        // 혹은 하드코딩된 경로 정보를 사용하는 방식으로 처리할 수 있습니다.
//        // 아래는 하드코딩된 경로 정보 예시 (외부 API 데이터와 일치하도록 조정 필요)
//        // "Gyeonggi-do (South)" → "Gyeonggi-do (North)", "Seoul", "Incheon", "Gangwon-do", "North Gyeongsang Province", "Daejeon", "Daegu"
//        saveRoute(hubMap.get("Gyeonggi-do (South)"), hubMap.get("Gyeonggi-do (North)"), 50L, 60);
//        saveRoute(hubMap.get("Gyeonggi-do (South)"), hubMap.get("Seoul"), 30L, 40);
//        saveRoute(hubMap.get("Gyeonggi-do (South)"), hubMap.get("Incheon"), 40L, 50);
//        saveRoute(hubMap.get("Gyeonggi-do (South)"), hubMap.get("Gangwon-do"), 100L, 120);
//        saveRoute(hubMap.get("Gyeonggi-do (South)"), hubMap.get("North Gyeongsang Province"), 200L, 180);
//        saveRoute(hubMap.get("Gyeonggi-do (South)"), hubMap.get("Daejeon"), 150L, 140);
//        saveRoute(hubMap.get("Gyeonggi-do (South)"), hubMap.get("Daegu"), 250L, 210);
//
//        // 나머지 경로 추가 (예시)
//        saveRoute(hubMap.get("Daejeon"), hubMap.get("Chungcheongnam-do"), 60L, 70);
//        saveRoute(hubMap.get("Daejeon"), hubMap.get("Chungcheongbuk-do"), 70L, 80);
//        saveRoute(hubMap.get("Daejeon"), hubMap.get("Sejong"), 20L, 30);
//        saveRoute(hubMap.get("Daejeon"), hubMap.get("Jeollabuk-do"), 90L, 100);
//        saveRoute(hubMap.get("Daejeon"), hubMap.get("Gwangju"), 140L, 150);
//        saveRoute(hubMap.get("Daejeon"), hubMap.get("Jeollanam-do"), 130L, 140);
//        saveRoute(hubMap.get("Daejeon"), hubMap.get("Gyeonggi-do (South)"), 150L, 140);
//        saveRoute(hubMap.get("Daejeon"), hubMap.get("Daegu"), 170L, 160);
//
//        saveRoute(hubMap.get("Daegu"), hubMap.get("North Gyeongsang Province"), 50L, 60);
//        saveRoute(hubMap.get("Daegu"), hubMap.get("South Gyeongsang Province"), 90L, 100);
//        saveRoute(hubMap.get("Daegu"), hubMap.get("Busan"), 120L, 130);
//        saveRoute(hubMap.get("Daegu"), hubMap.get("Ulsan"), 110L, 120);
        // 등 필요한 경로들을 추가...

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
