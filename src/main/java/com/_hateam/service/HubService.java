package com._hateam.service;

import com._hateam.dto.HubRequestDto;
import com._hateam.dto.HubDto;
import com._hateam.entity.Hub;
import com._hateam.global.dto.CreatedInfo;
import com._hateam.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;
    private void validateDuplicateHub(HubRequestDto requestDto) {
        hubRepository.findByNameAndDeletedAtIsNull(requestDto.getName())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("중복된 허브가 존재합니다.");
                });
    }

    public List<HubDto> getAllHubs(int page, int size, String sortBy, boolean isAsc) {
    }

    public HubDto createHub(HubRequestDto requestDto) {
        validateDuplicateHub(requestDto);
        Hub hub = createHubEntity(requestDto);
        hubRepository.save(hub);
        return HubDto.fromEntity(hub);
    }

    public HubDto getHub(Long id) {
    }

    public HubDto updateHub(Long id, HubRequestDto requestDto) {
        return null;
    }

    public void deleteHub(Long id) {
    }

    private Hub createHubEntity(HubRequestDto requestDto) {
        Hub hub = Hub.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .build();

        // 시큐리티 컨텍스트에서 인증 정보를 가져와 createdBy 필드 설정
        CreatedInfo createdInfo = new CreatedInfo();
        hub.setCreatedBy(createdInfo.getCreatedBy());
        hub.setCreatedAt(createdInfo.getCreatedAt());
        return hub;
    }
}
