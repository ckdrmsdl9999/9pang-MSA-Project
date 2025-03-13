package com._hateam.controller;

import com._hateam.dto.HubCreateRequestDto;
import com._hateam.dto.HubDto;
import com._hateam.dto.HubUpdateRequestDto;
import com._hateam.global.dto.ResponseDto;
import com._hateam.service.HubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    /**
     * 새로운 허브 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<HubDto>> createHub(
            @RequestBody @Valid HubCreateRequestDto requestDto) {

        HubDto hub = hubService.createHub(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(HttpStatus.CREATED, hub));
    }

    /**
     * 전체 허브 목록 조회 (페이지네이션 지원)
     * 예시: GET /hub?page=0&size=10&sortBy=createdAt&isAsc=false
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<HubDto>>> getAllHubs(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        List<HubDto> hubs = hubService.getAllHubs(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, hubs));
    }


    /**
     * 특정 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<HubDto>> getHub(@PathVariable Long id) {
        HubDto hub = hubService.getHub(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, hub));
    }

    /**
     * 특정 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto<HubDto>> updateHub(
            @PathVariable Long id,
            @RequestBody @Valid HubUpdateRequestDto requestDto) {

        HubDto updateHub = hubService.updateHub(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, updateHub));
    }

    /**
     * 특정 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deleteHub(@PathVariable Long id) {
        hubService.deleteHub(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, "Hub deleted successfully"));
    }
}
