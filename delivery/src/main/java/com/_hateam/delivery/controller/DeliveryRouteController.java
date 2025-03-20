package com._hateam.delivery.controller;

import com._hateam.common.dto.ResponseDto;
import com._hateam.delivery.dto.request.UpdateDeliveryRouteRequestDto;
import com._hateam.delivery.dto.response.DeiiveryRouteResponseDto;
import com._hateam.delivery.service.DeliveryRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

// todo: NoResourceFoundException 500 -> badrequest 수정 필요
@RestController
@RequestMapping("/api/deliveryRoutes") // todo: delivery에 종속될 필요 없이 특정 허브에서 대기중인 모든 배송을 조회 가능해야 겠지?
@RequiredArgsConstructor
@Validated
public class DeliveryRouteController {

    private final DeliveryRouteService deliveryRouteService;

    /**
     * 배송경로 전체 조회 -x
     * todo: 전제조회의 경우 사용할 일이 거의 없을것으로 예상
     */

    /**
     * 배송경로 전체 검색
     * todo: 각 허브가 본인들이 배송해야할 목록을 조회할 수 있음 hubtohub배송 배송담당자 배정시 사용될 수 있음
     */

    /**
     * 배송경로 상세 조회
     * todo: master를 제외하면 본인의 것만 보이도록 해야함 master는 전부가 보이도록 우선은 master 기준으로 작성
     */
    @GetMapping("/{deliveryRouteId}")
    public ResponseDto<DeiiveryRouteResponseDto> getDelivery(
            @PathVariable final UUID deliveryRouteId
    ) {
        return ResponseDto.success(deliveryRouteService.getDeliveryRouteForMaster(deliveryRouteId));
    }



    /**
     * 배송경로 생성 -x
     * todo: delivery 생성시 자동 생성, 직접생성은 매우 드문 경우
     */

    /**
     * 배송경로 수정
     * todo: master, hub, deliverer 가능 다만 deleverer의 경우 status만 수정 가능 우선 master 기준 작성
     */
    @PutMapping("/{deliveryRouteId}")
    public ResponseDto<Void> updateDelivery(
            @PathVariable final UUID deliveryRouteId,
            @RequestBody final UpdateDeliveryRouteRequestDto updateDeliveryRouteRequestDto
    ) {
        URI location = deliveryRouteService.updateDeliveryRouteForMaster(deliveryRouteId, updateDeliveryRouteRequestDto);
        return ResponseDto.responseWithLocation(HttpStatus.OK, location, "배송 수정 성공");
    }

    /**
     * 배송경로 삭제
     * todo: master, hub만 가능 우선 master 기준 작성, auditing 관련 확인 필요
     */
    @DeleteMapping("/{deliveryRouteId}")
    public ResponseDto<Void> deleteDelivery(
            @PathVariable final UUID deliveryRouteId
    ) {
        deliveryRouteService.deleteDeliveryRouteForMaster(deliveryRouteId);
        return ResponseDto.responseWithNoData(HttpStatus.OK, "배송정보 삭제 성공");
    }
}
