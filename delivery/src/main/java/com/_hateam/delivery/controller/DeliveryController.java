package com._hateam.delivery.controller;

import com._hateam.common.dto.PageResponseDto;
import com._hateam.common.dto.ResponseDto;
import com._hateam.delivery.dto.request.RegisterDeliveryRequestDto;
import com._hateam.delivery.dto.request.UpdateDeliveryRequestDto;
import com._hateam.delivery.dto.request.UpdateDeliveryStautsRequestDto;
import com._hateam.delivery.dto.response.DeliveryResponseDto;
import com._hateam.delivery.entity.DeliveryStatus;
import com._hateam.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Validated
public class DeliveryController {
    private final DeliveryService deliveryService;

    /**
     * 배송 전체 조회
     * todo: master를 제외하면 본인의 것만 보이도록 해야함 master는 전부가 보이도록 우선은 master 기준으로 작성
     * todo: page 추가, pageresponsedto 관련 고민, 로그인한 user 정보 가져오는법
     */
    @GetMapping
    public PageResponseDto<DeliveryResponseDto> getDeliveryList(
            Pageable pageable
    ) {
        Page<DeliveryResponseDto> page = deliveryService.findDeliveryListForMaster(pageable);
        return PageResponseDto.success(HttpStatus.OK, page, "배송 전체 조회 성공");
    }

    /**
     * 배송 전체 검색
     * todo: master를 제외하면 본인의 것만 보이도록 해야함 master는 전부가 보이도록 우선은 master 기준으로 작성
     * todo: 특정 허브로 배송될 값을 기준으로 검색 가능하게 해야하나?
     */
    @GetMapping("/search")
    public PageResponseDto<DeliveryResponseDto> searchDeliveryList(
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) String keyword, // id 값중 일부로 검색
            Pageable pageable
    ) {
        Page<DeliveryResponseDto> page = deliveryService.searchDeliveryListForMaster(pageable, status, keyword);
        return PageResponseDto.success(HttpStatus.OK, page, "배송 전체 조회 성공");
    }

    /**
     * 배송 상세 조회
     * todo: master를 제외하면 본인의 것만 보이도록 해야함 master는 전부가 보이도록 우선은 master 기준으로 작성
     */
    @GetMapping("/{deliveryId}")
    public ResponseDto<DeliveryResponseDto> getDelivery(
            @PathVariable final UUID deliveryId
    ) {
        return ResponseDto.success(deliveryService.getDeliveryForMaster(deliveryId));
    }



    /**
     * 배송 생성
     * todo: master만 가능 + 내부서비스에서 자동 생성 우선 master 기준 작성
     */
    @PostMapping
    public ResponseDto<Void> registerDelivery(
            @RequestBody @Valid final RegisterDeliveryRequestDto registerDeliveryRequestDto
    ) {
        URI location = deliveryService.registerDeliveryForMaster(registerDeliveryRequestDto);
        return ResponseDto.responseWithLocation(HttpStatus.CREATED, location, "배송 생성 성공");
    }

    /**
     * 배송 수정
     * todo: master, hub, deliverer 가능 다만 deleverer의 경우 status만 수정 가능 우선 master 기준 작성
     */
    @PutMapping("/{deliveryId}")
    public ResponseDto<Void> updateDelivery(
            @PathVariable final UUID deliveryId,
            @RequestBody final UpdateDeliveryRequestDto updateDeliveryRequestDto
    ) {
        URI location = deliveryService.updateDeliveryForMaster(deliveryId, updateDeliveryRequestDto);
        return ResponseDto.responseWithLocation(HttpStatus.OK, location, "배송 수정 성공");
    }

    /**
     * 배송 상태 수정
     * todo: master, hub, deliverer 가능 다만 deleverer의 경우 status만 수정 가능 우선 master 기준 작성
     */
    @PutMapping("/{deliveryId}/status")
    public ResponseDto<Void> updateDeliveryStatus(
            @PathVariable final UUID deliveryId,
            @RequestBody final UpdateDeliveryStautsRequestDto updateDeliveryStautsRequestDto
    ) {
        URI location = deliveryService.updateDeliveryStatus(deliveryId, updateDeliveryStautsRequestDto.getStatus());
        return ResponseDto.responseWithLocation(HttpStatus.OK, location, "배송 수정 성공");
    }

    /**
     * 배송 삭제
     * todo: master, hub만 가능 우선 master 기준 작성
     */
    @DeleteMapping("/{deliveryId}")
    public ResponseDto<Void> deleteDelivery(
            @PathVariable final UUID deliveryId
    ) {
        deliveryService.deleteDeliveryForMaster(deliveryId);
        return ResponseDto.responseWithNoData(HttpStatus.OK, "배송정보 삭제 성공");
    }
}
