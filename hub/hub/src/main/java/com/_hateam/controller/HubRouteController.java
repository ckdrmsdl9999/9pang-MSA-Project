package com._hateam.controller;

import com._hateam.common.dto.ResponseDto;
import com._hateam.dto.HubRouteDto;
import com._hateam.dto.HubRouteRequestDto;
import com._hateam.service.HubRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/hub-routes")
@RequiredArgsConstructor
public class HubRouteController {

    private final HubRouteService hubRouteService;

    /**
     * 새로운 허브 루트 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<HubRouteDto>> createHubRoute(
            @RequestBody @Valid HubRouteRequestDto requestDto) {

        HubRouteDto hubRouteDto = hubRouteService.createHubRoute(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(HttpStatus.CREATED, hubRouteDto));
    }

    /**
     * 전체 허브 루트 목록 조회 (페이지네이션 지원)
     * 예시: GET /hub?page=0&size=10&sortBy=createdAt&isAsc=false
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<HubRouteDto>>> getAllHubRoutes(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        List<HubRouteDto> hubRouteDto = hubRouteService.getAllHubRoutes(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, hubRouteDto));
    }


    /**
     * 특정 허브 루트 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<HubRouteDto>> getHubRoute(@PathVariable UUID id) {
        HubRouteDto hubRouteDto = hubRouteService.getHubRoute(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, hubRouteDto));
    }

    @GetMapping("/cache")
    public ResponseEntity<ResponseDto<HubRouteDto>> getHubRoute(@RequestParam("sourceHub") String sourceHub,
                                                                @RequestParam("destinationHub") String destinationHub) {
        HubRouteDto hubRouteDto = hubRouteService.getHubRoute(sourceHub, destinationHub);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, hubRouteDto));
    }

    /**
     * 특정 허브 루트 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto<HubRouteDto>> updateHubRoute(
            @PathVariable UUID id,
            @RequestBody @Valid HubRouteRequestDto requestDto) {
        HubRouteDto hubRouteDto = hubRouteService.updateHubRoute(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, hubRouteDto));
    }

    /**
     * 특정 허브 루트 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deleteHubRoute(@PathVariable UUID id) {
        hubRouteService.deleteHubRoute(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, "HubRoute deleted successfully"));
    }
}
