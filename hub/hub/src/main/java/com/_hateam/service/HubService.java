package com._hateam.service;

import com._hateam.dto.HubDto;
import com._hateam.dto.HubRequestDto;
import com._hateam.entity.Hub;
import com._hateam.repository.HubRepository;
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
public class HubService {

    private final HubRepository hubRepository;

    @Transactional
    public HubDto createHub(HubRequestDto requestDto) {
        validateDuplicateHub(requestDto);
        Hub hub = createHubEntity(requestDto);
        hubRepository.save(hub);
        return HubDto.hubToHubDto(hub);
    }

    @Transactional(readOnly = true)
    public List<HubDto> getAllHubs(int page, int size, String sortBy, boolean isAsc) {
        List<HubDto> hubDtoList = new ArrayList<>();
        List<Hub> hubList = hubInfoPaging(page, size, sortBy, isAsc);
        hubDtoList = hubList.stream().map(HubDto::hubToHubDto).collect(Collectors.toList());
        return hubDtoList;
    }


    @Transactional(readOnly = true)
    public HubDto getHub(UUID id) {
        Hub hub = findHub(id);
        return HubDto.hubToHubDto(hub);
    }

    @Transactional
    public HubDto updateHub(UUID id, HubRequestDto requestDto) {
        Hub hub = findHub(id);
        updateHubInfo(hub, requestDto);
        return HubDto.hubToHubDto(hub);
    }

    @Transactional
    public void deleteHub(UUID id) {
        Hub hub = findHub(id);
        hubRepository.delete(hub);
    }

    private void validateDuplicateHub(HubRequestDto requestDto) {
        hubRepository.findByNameAndDeletedAtIsNull(requestDto.getName()).ifPresent(m -> {
            throw new IllegalArgumentException("중복된 허브가 존재합니다.");
        });
    }

    private Hub createHubEntity(HubRequestDto requestDto) {
        Hub hub = Hub.builder().name(requestDto.getName()).address(requestDto.getAddress()).latitude(requestDto.getLatitude()).longitude(requestDto.getLongitude()).build();

//        // 시큐리티 컨텍스트에서 인증 정보를 가져와 createdBy 필드 설정
//            추후 시큐리티 적용시 다시 수정
//        CreatedInfo createdInfo = new CreatedInfo();
//        hub.setCreatedBy(createdInfo.getCreatedBy());
//        hub.setCreatedAt(createdInfo.getCreatedAt());
        return hub;
    }

    private List<Hub> hubInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        // 10, 30, 50 외의 size는 기본 10으로 설정
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 전체 hub 수 조회
        long totalHubs = hubRepository.count();
        // 전체 페이지 수 계산 (0페이지부터 시작하므로)
        int totalPages = (int) Math.ceil((double) totalHubs / size);

        // 요청한 페이지 번호가 전체 페이지 수 이상이면 예외 처리
        if (page >= totalPages && totalHubs > 0) {
            throw new IllegalArgumentException("요청한 페이지 번호(" + page + ")가 전체 페이지 수(" + totalPages + ")를 초과합니다.");
        }

        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return hubRepository.findAll(pageable).getContent();
    }

    private Hub findHub(UUID id) {
        return hubRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Hub not found with id: " + id));
    }

    private void updateHubInfo(Hub hub, HubRequestDto requestDto) {
        hub.setName(requestDto.getName());
        hub.setAddress(requestDto.getAddress());
        hub.setLatitude(requestDto.getLatitude());
        hub.setLongitude(requestDto.getLongitude());
    }
}
