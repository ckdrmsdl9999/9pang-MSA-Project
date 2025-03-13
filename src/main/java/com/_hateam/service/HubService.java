package com._hateam.service;

import com._hateam.dto.HubCreateRequestDto;
import com._hateam.dto.HubDto;
import com._hateam.dto.HubUpdateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HubService {
    public List<HubDto> getAllHubs(int page, int size, String sortBy, boolean isAsc) {
    }

    public HubDto createHub(HubCreateRequestDto requestDto) {
    }

    public HubDto getHub(Long id) {
    }

    public HubDto updateHub(Long id, HubUpdateRequestDto requestDto) {
        return null;
    }

    public void deleteHub(Long id) {
    }
}
