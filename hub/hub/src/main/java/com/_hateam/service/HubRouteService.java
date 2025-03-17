package com._hateam.service;

import com._hateam.dto.HubRouteDto;
import com._hateam.dto.HubRouteRequestDto;
import com._hateam.entity.Hub;
import com._hateam.entity.HubRoute;
import com._hateam.repository.HubRepository;
import com._hateam.repository.HubRouteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository; // Hub 엔티티 조회를 위한 Repository

    @Transactional
    public HubRouteDto createHubRoute(HubRouteRequestDto requestDto) {
        // 출발지와 도착지 ID가 동일하면 예외 발생
        validateSourceAndDestinationDifferent(requestDto);

        // 출발지와 도착지 Hub 객체 조회
        Hub sourceHub = getHubById(requestDto.getSourceHubId(), "출발지");
        Hub destinationHub = getHubById(requestDto.getDestinationHubId(), "도착지");

        // HubRoute 엔티티 생성 (선택적 필드 포함)
        HubRoute hubRoute = HubRoute.builder()
                .sourceHub(sourceHub)
                .destinationHub(destinationHub)
                .distanceKm(requestDto.getDistanceKm())
                .estimatedTimeMinutes(requestDto.getEstimatedTimeMinutes())
                .build();

        hubRouteRepository.save(hubRoute);
        return HubRouteDto.fromEntity(hubRoute);
    }


    @Transactional(readOnly = true)
    public List<HubRouteDto> getAllHubRoutes(int page, int size, String sortBy, boolean isAsc) {
        // 페이징, 정렬 처리
        List<HubRoute> hubRouteList = hubInfoPaging(page, size, sortBy, isAsc);
        return hubRouteList.stream()
                .map(HubRouteDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HubRouteDto getHubRoute(UUID id) {
        HubRoute hubRoute = findHubRoute(id);
        return HubRouteDto.fromEntity(hubRoute);
    }

    @Transactional
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

        return HubRouteDto.fromEntity(hubRoute);
    }


    @Transactional
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
}
