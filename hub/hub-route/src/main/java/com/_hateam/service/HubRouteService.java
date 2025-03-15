package com._hateam.service;

import com._hateam.dto.HubRouteDto;
import com._hateam.dto.HubRouteRequestDto;
import com._hateam.entity.HubRoute;
import com._hateam.repository.HubRouteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;

    @Transactional
    public HubRouteDto createHub(HubRouteRequestDto requestDto) {
        validateDuplicateHub(requestDto);
        HubRoute hubRoute = createHubEntity(requestDto);
        hubRouteRepository.save(hubRoute);
        return HubRouteDto.hubToHubDto(hubRoute);
    }

    @Transactional(readOnly = true)
    public List<HubRouteDto> getAllHubs(int page, int size, String sortBy, boolean isAsc) {
        List<HubRouteDto> hubDtoList = new ArrayList<>();
        List<HubRoute> hubRouteList = hubInfoPaging(page, size, sortBy, isAsc);
        hubDtoList = hubRouteList.stream().map(HubRouteDto::hubToHubDto).collect(Collectors.toList());
        return hubDtoList;
    }


    @Transactional(readOnly = true)
    public HubRouteDto getHub(UUID id) {
        HubRoute hubRoute = findHub(id);
        return HubRouteDto.hubToHubDto(hubRoute);
    }

    @Transactional
    public HubRouteDto updateHub(UUID id, HubRouteRequestDto requestDto) {
        HubRoute hubRoute = findHub(id);
        updateHubInfo(hubRoute, requestDto);
        return HubRouteDto.hubToHubDto(hubRoute);
    }

    @Transactional
    public void deleteHub(UUID id) {
        HubRoute hubRoute = findHub(id);
        hubRouteRepository.delete(hubRoute);
    }

    private void validateDuplicateHub(HubRouteRequestDto requestDto) {
        hubRouteRepository.findByNameAndDeletedAtIsNull(requestDto.getName()).ifPresent(m -> {
            throw new IllegalArgumentException("중복된 허브가 존재합니다.");
        });
    }

    private HubRoute createHubEntity(HubRouteRequestDto requestDto) {
        HubRoute hubRoute = HubRoute.builder().name(requestDto.getName()).address(requestDto.getAddress()).latitude(requestDto.getLatitude()).longitude(requestDto.getLongitude()).build();

//        // 시큐리티 컨텍스트에서 인증 정보를 가져와 createdBy 필드 설정
//            추후 시큐리티 적용시 수정
//        CreatedInfo createdInfo = new CreatedInfo();
//        hubRoute.setCreatedBy(createdInfo.getCreatedBy());
//        hubRoute.setCreatedAt(createdInfo.getCreatedAt());
        return hubRoute;
    }

    private List<HubRoute> hubInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        // 10, 30, 50 외의 size는 기본 10으로 설정
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 전체 hub 수 조회
        long totalHubs = hubRouteRepository.count();
        // 전체 페이지 수 계산 (0페이지부터 시작하므로)
        int totalPages = (int) Math.ceil((double) totalHubs / size);

        // 요청한 페이지 번호가 전체 페이지 수 이상이면 예외 처리
        if (page >= totalPages && totalHubs > 0) {
            throw new IllegalArgumentException("요청한 페이지 번호(" + page + ")가 전체 페이지 수(" + totalPages + ")를 초과합니다.");
        }

        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return hubRouteRepository.findAll(pageable).getContent();
    }

    private HubRoute findHub(UUID id) {
        return hubRouteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("HubRoute not found with id: " + id));
    }

    private void updateHubInfo(HubRoute hubRoute, HubRouteRequestDto requestDto) {
        hubRoute.setName(requestDto.getName());
        hubRoute.setAddress(requestDto.getAddress());
        hubRoute.setLatitude(requestDto.getLatitude());
        hubRoute.setLongitude(requestDto.getLongitude());
    }
}
