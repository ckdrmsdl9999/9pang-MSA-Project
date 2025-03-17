package com._hateam.delivery.controller;

import com._hateam.common.dto.PageResponseDto;
import com._hateam.common.dto.ResponseDto;
import com._hateam.delivery.dto.request.RegisterDeliveryRequestDto;
import com._hateam.delivery.dto.request.UpdateDeliveryRequestDto;
import com._hateam.delivery.dto.response.DeliveryResponseDto;
import com._hateam.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Validated
public class DeliveryController {
    private final DeliveryService deliveryService;

    /**
     * 배송 전체 조회
     * todo: master를 제외하면 본인의 것만 보이도록 해야함 master는 전부가 보이도록 우선은 master 기준으로 작성
     * todo: page 추가, pageresponsedto 관련 고민, 로그인한 user 정보 가져오는법
     */
//    @GetMapping
//    public ResponseEntity<PageResponseDto<DeliveryResponseDto>> getDeliveryList(
//            Pageable pageable
//    ) {
//        return ResponseEntity.ok(deliveryService.findDeliveryListForMaster(pageable));
//    }

    /**
     * 배송 전체 검색
     * todo: master를 제외하면 본인의 것만 보이도록 해야함 master는 전부가 보이도록 우선은 master 기준으로 작성, 추후 page 추가
     */
//    @GetMapping("/search")
//    public ResponseEntity<ResponseDto<DeliveryResponseDto>> searchDeliveryList(
//            @RequestParam @NotBlank(message = "status를 입력해주세요.") String status,
//            @RequestParam @NotBlank(message = "검색어를 입력해주세요.") String keyword,
//            Pageable pageable
//    ) {
//        return ResponseEntity.ok(deliveryService.searchDeliveryListForAdmin(pageable, status, keyword));
//    }

    /**
     * 배송 상세 조회
     * todo: master를 제외하면 본인의 것만 보이도록 해야함 master는 전부가 보이도록 우선은 master 기준으로 작성
     */
    @GetMapping("/{delivery_id}")
    public ResponseDto<DeliveryResponseDto> getDelivery(
            @PathVariable final UUID delivery_id
    ) {
        return ResponseDto.success(deliveryService.getDeliveryForMaster(delivery_id));
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
    @PutMapping("/{delivery_id}")
    public ResponseDto<Void> updateDelivery(
            @PathVariable final UUID delivery_id,
            @RequestBody final UpdateDeliveryRequestDto updateDeliveryRequestDto
    ) {
        URI location = deliveryService.updateDeliveryForMaster(delivery_id, updateDeliveryRequestDto);
        return ResponseDto.responseWithLocation(HttpStatus.OK, location, "배송 수정 성공");
    }

    /**
     * 배송 삭제
     * todo: master, hub만 가능 우선 master 기준 작성
     */
    @DeleteMapping("/{delivery_id}")
    public ResponseDto<Void> deleteDelivery(
            @PathVariable final UUID delivery_id
    ) {
        deliveryService.deleteDeliveryForMaster(delivery_id);
        return ResponseDto.responseWithNoData(HttpStatus.OK, "배송정보 삭제 성공");
    }
}
