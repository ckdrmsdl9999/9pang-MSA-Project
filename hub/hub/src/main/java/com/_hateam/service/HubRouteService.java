package com._hateam.service;

import com._hateam.dto.HubDto;
import com._hateam.dto.HubRouteDto;
import com._hateam.dto.HubRouteRequestDto;
import com._hateam.entity.Hub;
import com._hateam.entity.HubRoute;
import com._hateam.repository.HubRepository;
import com._hateam.repository.HubRouteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "hub-route")
public class HubRouteService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository; // Hub 엔티티 조회를 위한 Repository

    @Transactional
    @CachePut(key = "#result.id")
    public HubRouteDto createHubRoute(HubRouteRequestDto requestDto) {
        // 출발지와 도착지 ID가 동일하면 예외 발생
        validateSourceAndDestinationDifferent(requestDto);

        // 출발지와 도착지 Hub 객체 조회
        Hub sourceHub = getHubById(requestDto.getSourceHubId(), "출발지");
        Hub destinationHub = getHubById(requestDto.getDestinationHubId(), "도착지");

        // HubRoute 엔티티 생성 (선택적 필드 및 Redis 저장 정보 포함)
        HubRoute hubRoute = HubRoute.builder()
                .sourceHub(sourceHub)
                .destinationHub(destinationHub)
                .distanceKm(requestDto.getDistanceKm())
                .estimatedTimeMinutes(requestDto.getEstimatedTimeMinutes())
                // Redis 저장 정보 추가
                .penaltyDistance(requestDto.getPenaltyDistance())
                .nodeDistances(requestDto.getNodeDistances())
                .route(requestDto.getRoute())
                .actualDistance(requestDto.getActualDistance())
                .totalCost(requestDto.getTotalCost())
                .cumulativeDistances(requestDto.getCumulativeDistances())
                .build();

        hubRouteRepository.save(hubRoute);
        return HubRouteDto.fromEntity(hubRoute);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'allHubRoutes_' + #page + '_' + #size + '_' + #sortBy + '_' + #isAsc")
    public List<HubRouteDto> getAllHubRoutes(int page, int size, String sortBy, boolean isAsc) {
        // 페이징, 정렬 처리
        List<HubRoute> hubRouteList = hubInfoPaging(page, size, sortBy, isAsc);
        return hubRouteList.stream()
                .map(HubRouteDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public HubRouteDto getHubRoute(UUID id) {
        HubRoute hubRoute = findHubRoute(id);
        return HubRouteDto.fromEntity(hubRoute);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hubRoutes", key = "#sourceHub + ':' + #destinationHub")
    public HubRouteDto getHubRoute(String sourceHub, String destinationHub) {
        String redisKey = generateRedisKey(sourceHub, destinationHub);
        Map<Object, Object> routeData =  redisTemplate.opsForHash().entries(redisKey);

        if (routeData == null || routeData.isEmpty()) {
            log.warn("Redis에 데이터가 없습니다. 데이터베이스에서 조회합니다.");
            return fetchRoute(sourceHub, destinationHub);
        }

        // Redis에서 조회한 HashMap을 HubRouteDto로 변환
        return mapToHubRouteDto(routeData);
    }

    @Transactional
    @CachePut(key = "#id")
    public HubRouteDto updateHubRoute(UUID id, HubRouteRequestDto requestDto) {
        HubRoute hubRoute = findHubRoute(id);

        // 출발지와 도착지 ID가 동일하면 예외 발생
        validateSourceAndDestinationDifferent(requestDto);

        // 출발지 허브 업데이트 (ID가 변경된 경우)
        if (requestDto.getSourceHubId() != null &&
                !requestDto.getSourceHubId().equals(hubRoute.getSourceHub().getId())) {
            Hub newSourceHub = getHubById(requestDto.getSourceHubId(), "출발지");
            hubRoute.setSourceHub(newSourceHub);
        }

        // 도착지 허브 업데이트 (ID가 변경된 경우)
        if (requestDto.getDestinationHubId() != null &&
                !requestDto.getDestinationHubId().equals(hubRoute.getDestinationHub().getId())) {
            Hub newDestinationHub = getHubById(requestDto.getDestinationHubId(), "도착지");
            hubRoute.setDestinationHub(newDestinationHub);
        }

        // 기타 필드 업데이트
        hubRoute.setDistanceKm(requestDto.getDistanceKm());
        hubRoute.setEstimatedTimeMinutes(requestDto.getEstimatedTimeMinutes());
        // Redis 저장 정보 업데이트
        hubRoute.setPenaltyDistance(requestDto.getPenaltyDistance());
        hubRoute.setNodeDistances(requestDto.getNodeDistances());
        hubRoute.setRoute(requestDto.getRoute());
        hubRoute.setActualDistance(requestDto.getActualDistance());
        hubRoute.setTotalCost(requestDto.getTotalCost());
        hubRoute.setCumulativeDistances(requestDto.getCumulativeDistances());

        return HubRouteDto.fromEntity(hubRoute);
    }

    @Transactional
    @CacheEvict(key = "#id")
    public void deleteHubRoute(UUID id) {
        HubRoute hubRoute = findHubRoute(id);
        hubRouteRepository.delete(hubRoute);
    }

    /**
     * 요청 DTO에서 출발지와 도착지 Hub ID가 동일하면 예외 발생.
     */
    private void validateSourceAndDestinationDifferent(HubRouteRequestDto requestDto) {
        if (requestDto.getSourceHubId().equals(requestDto.getDestinationHubId())) {
            throw new IllegalArgumentException("출발지와 도착지는 동일할 수 없습니다.");
        }
    }

    private String generateRedisKey(String source, String destination) {
        return "route:" + source + ":" + destination;
    }

    private HubRouteDto fetchRoute(String source, String destination) {
        // 입력 값에 포함된 불필요한 큰따옴표를 제거하고 공백을 제거합니다.
        String cleanSource = source.replace("\"", "").trim();
        String cleanDestination = destination.replace("\"", "").trim();

        Hub sourceHub = hubRepository.findByNameAndDeletedAtIsNull(cleanSource)
                .orElseThrow(() -> new EntityNotFoundException("출발지 허브를 찾을 수 없습니다. 이름: " + cleanSource));
        Hub destinationHub = hubRepository.findByNameAndDeletedAtIsNull(cleanDestination)
                .orElseThrow(() -> new EntityNotFoundException("도착지 허브를 찾을 수 없습니다. 이름: " + cleanDestination));

        // 경로 조회
        HubRoute hubRoute = hubRouteRepository.findBySourceHubAndDestinationHub(sourceHub, destinationHub);
        if (hubRoute != null) {
            return HubRouteDto.fromEntity(hubRoute);
        }
        return null;
    }

    private Hub getHubById(UUID hubId, String hubType) {
        return hubRepository.findById(hubId)
                .orElseThrow(() -> new EntityNotFoundException(hubType + " 허브를 찾을 수 없습니다. ID: " + hubId));
    }

    private List<HubRoute> hubInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        long totalRoutes = hubRouteRepository.count();
        int totalPages = (int) Math.ceil((double) totalRoutes / size);

        if (page >= totalPages && totalRoutes > 0) {
            throw new IllegalArgumentException("요청한 페이지 번호(" + page + ")가 전체 페이지 수(" + totalPages + ")를 초과합니다.");
        }
// sortBy 파라미터가 "updatedAt"이면 updatedAt, 그 외는 createdAt으로 정렬
        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return hubRouteRepository.findAll(pageable).getContent();
    }


    private HubRoute findHubRoute(UUID id) {

        return hubRouteRepository.findById(id)

                .orElseThrow(() -> new EntityNotFoundException("HubRoute not found with id: " + id));

    }
    private HubRouteDto mapToHubRouteDto(Map<Object, Object> routeData) {
        if (routeData == null || routeData.isEmpty()) {
            return null; // 또는 예외 처리
        }
        return HubRouteDto.builder()
                .id(UUID.fromString(routeData.get("id").toString()))
                .sourceHub(mapToHubDto((Map<String, Object>) routeData.get("sourceHub")))
                .destinationHub(mapToHubDto((Map<String, Object>) routeData.get("destinationHub")))
                .distanceKm(Long.parseLong(routeData.get("distanceKm").toString()))
                .estimatedTimeMinutes(Integer.parseInt(routeData.get("estimatedTimeMinutes").toString()))
                .penaltyDistance(Double.parseDouble(routeData.get("penaltyDistance").toString()))
                .nodeDistances((List<Double>) routeData.get("nodeDistances"))
                .route((List<String>) routeData.get("route"))
                .actualDistance(Double.parseDouble(routeData.get("actualDistance").toString()))
                .totalCost(Double.parseDouble(routeData.get("totalCost").toString()))
                .cumulativeDistances((List<Double>) routeData.get("cumulativeDistances"))
                .build();
    }

    private HubDto mapToHubDto(Map<String, Object> hubData) {
        if (hubData == null || hubData.isEmpty()) {
            return null; // 또는 예외 처리
        }
        return HubDto.builder()
                .id(UUID.fromString(hubData.get("id").toString()))
                .name(hubData.get("name").toString())
                .address(hubData.get("address").toString())
                .latitude(hubData.get("latitude").toString())
                .longitude(hubData.get("longitude").toString())
                .build();
    }
}