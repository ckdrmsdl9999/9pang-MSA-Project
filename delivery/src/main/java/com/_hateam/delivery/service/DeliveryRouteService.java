package com._hateam.delivery.service;

import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.delivery.dto.request.UpdateDeliveryRouteRequestDto;
import com._hateam.delivery.dto.response.DeiiveryRouteResponseDto;
import com._hateam.delivery.dto.response.UserClientDeliverResponseDto;
import com._hateam.delivery.entity.Delivery;
import com._hateam.delivery.entity.DeliveryRoute;
import com._hateam.delivery.entity.DeliveryStatus;
import com._hateam.delivery.feignClient.UserClient;
import com._hateam.delivery.repository.DeliveryRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryService deliveryService;
    private final UserClient userClient;
    private final DeliveryKafkaService deliveryKafkaService;

    /**
     * 배송 경로 생성 - x
     * todo: 기본적으로는 배송생성시 동시생성, 특정 허브에 문제가 생겼거나 하는 극단적인 예외 발생
     * todo: 배송경로를 수동으로 수정, 추가 하는경우, 배송경로 생성, 수정이 필요할 수 있음
     */

    /**
     * 배송경로 조회
     */
    @Transactional(readOnly = true)
    public DeiiveryRouteResponseDto getDeliveryRouteForMaster(UUID deliveryRouteId) {
        DeliveryRoute deliveryRoute = checkDeliveryRoute(deliveryRouteId);
        return DeiiveryRouteResponseDto.from(deliveryRoute);
    }

    /**
     * 배송경로 수정
     * todo: 일반적인 경우 status, 배송담당자 수정만으로 충분, 전체적인 수정은 극단적 상황
     *  배송경로의 상태가 변경될때 다음 배송경로의 상태가 변경될 필요가 있음 1배송 완료시 2배송 대기에서 준비로 바뀌어야 함
     *  배송담당자 조회 필요
     */
    @Transactional
    public URI updateDeliveryRouteForMaster(UUID deliveryRouteId, UpdateDeliveryRouteRequestDto updateDeliveryRouteRequestDto) {
        // deliveryRoute 상태 변경
        DeliveryRoute deliveryRoute = checkDeliveryRoute(deliveryRouteId);
        deliveryRoute.updateStatusOf(updateDeliveryRouteRequestDto);

        DeliveryStatus status = updateDeliveryRouteRequestDto.getStatus();
        Delivery delivery = deliveryRoute.getDelivery();
        UUID deliveryId = delivery.getId();
        DeliveryStatus deliveryStatus = delivery.getStatus();

        switch (status) {
            case MOVING_TO_HUB -> {
                // delivery 상태 변경
                // todo: order상태 변경
                if (deliveryStatus != DeliveryStatus.MOVING_TO_HUB) {
                    delivery.updateStatusOf(status);
                    deliveryKafkaService.orderUpdateByKafka(delivery);
                }
            }
            case ARRIVED_AT_DEST_HUB -> {
                // nextDeliveryRoute 조회
                DeliveryRoute nextDeliveryRoute = deliveryRouteRepository.findByDeliveryAndSequence(delivery,
                        deliveryRoute.getSequence()+1
                ).orElse(null);

                if (nextDeliveryRoute != null) {
                    // nextDeliveryRoute에 배송담당자 배정
                    UserClientDeliverResponseDto userClientDeliverResponseDto = userClient
                            .getDeliverAssign("DELIVER_HUB", null)
                            .getBody()
                            .getData();

                    nextDeliveryRoute.updateDeliver(userClientDeliverResponseDto);

                } else {
                    // hub배송 완료, 배송상태변경 + 업체 배송담당자 배정
                    // 허브 배송 완료, 업체 배송 대기로 상태 변경
                    delivery.updateStatusOf(status);
                    // 업체 배송담당자 배정
                    UserClientDeliverResponseDto userClientDeliverResponseDto = userClient
                            .getDeliverAssign("DELIVER_COMPANY", delivery.getEndHubId())
                            .getBody()
                            .getData();

                    delivery.updateDeliver(userClientDeliverResponseDto);
                }
            }
        }

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(deliveryRoute.getId())
                .toUri();
    }

    /**
     * 배송경로 삭제 - 일반적인 경우 거의 쓰이지 않음
     */
    @Transactional
    public void deleteDeliveryRouteForMaster(UUID deliveryRouteId) {
        DeliveryRoute deliveryRoute = checkDeliveryRoute(deliveryRouteId);
        deliveryRoute.deleteOf("deleter"); // todo: 추후에 로그인한 사람으로 수정 필요 생성, 수정자도 관련 로직 필요
    }

    /**
     * 배송경로 검색
     */

    

    


    /*내부 메서드------------------------------------------------------------------------------------------------------*/

    /**
     * 조회시 check사항
     */
    private DeliveryRoute checkDeliveryRoute(final UUID deliveryRouteId) {
        return deliveryRouteRepository.findByIdAndDeletedAtIsNull(deliveryRouteId)
                .orElseThrow(() -> new CustomNotFoundException("존재하지 않는 배송경로정보입니다."));
    }
}
