package com._hateam.service;

import com._hateam.dto.HubCreateRequestDto;
import com._hateam.dto.HubDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HubService {
    public List<HubDto> getAllPosts(int page, int size, String sortBy, boolean isAsc) {
    }

    public HubDto createPost(HubCreateRequestDto requestDto) {
    }

    public HubDto getPost(Long id) {
    }
}
