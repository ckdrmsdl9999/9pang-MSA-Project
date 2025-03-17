package com._hateam.delivery.service;

import com._hateam.common.exception.CustomConflictException;
import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.delivery.dto.request.RegisterDeliveryRequestDto;
import com._hateam.delivery.dto.request.UpdateDeliveryRequestDto;
import com._hateam.delivery.dto.response.CompanyResponseDto;
import com._hateam.delivery.dto.response.DeliveryResponseDto;
import com._hateam.delivery.dto.response.OrderResponseDto;
import com._hateam.delivery.dto.response.UserResponseDto;
import com._hateam.delivery.entity.Delivery;
import com._hateam.delivery.entity.OrderStatus;
import com._hateam.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    /**
     * todo: order로 부터 가져올것 : 허브id, 회사id, 주문요청(현재 테이블이나 entity에는 없다),
     * todo: 배송마감(어느 배송마감인지 자세한 정보 확인), 상태(현재 배송준비가 다 되었는지)
     * todo: companyid를 통해 회사의 주소값(수령인 주소), 수령인명, 수령인 slackid
     * todo: 수령인 주소를 통해 가장 가까운 허브의 위치 알아내서 최종목적지hubid 확인
     * todo: 최종목적지hubid를 통해 각허브경로 가져오기 + 그 경로대로 허브 경로 기록 생성
     * todo: 각 경로 기록마다의 예상이동거리, 시간 확인, 배송상태 기본 전부 대기중으로 생성 예상이동거리, 시간의 경우 허브경로쪽에서 가지고 있겠지...?
     * todo: 1번 배송담당자가 전부 부담 -> 돌아가면서 부담 -> 정해진 시간에 배송상태들을 확인하여 같은 hub 끼리 묶어서 배정
     * todo: 필요시 상태값을 좀더 자세히 표현할 필요가 있음... 허브에 들어와서 대기중인지, 아직 허브까지 배송이 안된건지
     * todo: transaction 과정에서 에러 발생으로 배송이 생성되지 않을때의 처리(주문 삭제 요청 or 다른 처리 방법)
     */
    @Transactional
    public URI registerDeliveryForMaster(RegisterDeliveryRequestDto registerDeliveryRequestDto) {
        // 이미 존재하는 값인지 확인
        checkDeliveryByOrderId(registerDeliveryRequestDto.getOrderId());
        // todo: order 조회 구현, 임시로 orderResponse 구성, 별도로 분리하는 쪽이 좋을듯
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderId(registerDeliveryRequestDto.getOrderId());
        orderResponseDto.setHubId(UUID.randomUUID());
        orderResponseDto.setCompanyId(UUID.randomUUID());
        orderResponseDto.setOrderRequest("임시 요청입니다.");
        orderResponseDto.setOrderStatus(OrderStatus.WAITING);

        // todo: company조회 구현, 임시 companyResponse 구성, 별도로 분리하는 쪽이 좋을듯
        CompanyResponseDto companyResponseDto = new CompanyResponseDto();
        companyResponseDto.setCompanyId(orderResponseDto.getCompanyId());
        companyResponseDto.setCompanyAddress("임시 주소입니다.");
        companyResponseDto.setUsername("tester");

        // todo: user조회 구현, 임시 user 구성, 별도로 분리하는 쪽이 좋을듯
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("tester");
        userResponseDto.setSlackId("testerSlack");
        userResponseDto.setUsernickname("테스트용");

        // todo: 현재 hub 기준 가장 가까운 허브 위치 찾기, 모든 주소를 저장하는 시점에 위 경도도 같이 저장될거라 우선 가정
        // todo: 그 위치기반으로 주변 위치 찾는 query가 있었는데... 그걸로 찾는다고 가정하고
        UUID destHubId = UUID.randomUUID();

        // todo: 배송담당자 배정 로직 예상하는 방식은 미배정후 매일 도착한 화물에 대하여 정해진 시간에 배정로직 진행
        String delivererUsername = "deliverer01";

        Delivery delivery = Delivery.addOf(orderResponseDto, companyResponseDto, userResponseDto, destHubId, delivererUsername);
        deliveryRepository.save(delivery);
        // todo: 배송경로 생성

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(delivery.getId())
                .toUri();
    }

    /**
     * 배송정보 상세 조회
     */
    public DeliveryResponseDto getDeliveryForMaster(UUID deliveryId) {
        Delivery delivery = checkDelivery(deliveryId);
        return DeliveryResponseDto.from(delivery);
    }

    /**
     * 배송정보 수정
     */
    @Transactional
    public URI updateDeliveryForMaster(UUID deliveryId, UpdateDeliveryRequestDto updateDeliveryRequestDto) {
        Delivery delivery = checkDelivery(deliveryId);
        delivery.updateOf(updateDeliveryRequestDto);

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(delivery.getId())
                .toUri();
    }

    /**
     * 배송정보 삭제
     */
    @Transactional
    public void deleteDeliveryForMaster(UUID deliveryId) {
        Delivery delivery = checkDelivery(deliveryId);
        delivery.deleteOf("deleter"); // todo: 추후에 로그인한 사람으로 수정 필요 생성, 수정자도 관련 로직 필요
    }

    /**
     * 조회시 check사항
     */
    private Delivery checkDelivery(final UUID deliveryId) {
        return deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomNotFoundException("존재하지 않는 배송정보입니다."));
    }
    /**
     * orderId를 통한 delivery 확인
     */
    private void checkDeliveryByOrderId(final UUID orderId) {
        boolean deliveryIsExists = deliveryRepository.existsByOrderIdAndDeletedAtIsNull(orderId);
        if (deliveryIsExists) {
            throw new CustomConflictException("주어진 주문에 대한 배송정보는 이미 존재합니다.");
        }
    }
}
