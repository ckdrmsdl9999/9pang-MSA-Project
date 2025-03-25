package com._hateam.order.domain.service;

import com._hateam.order.domain.model.Order;
import com._hateam.order.domain.model.OrderProduct;
import com._hateam.order.domain.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderDomainService {

    /**
     * 새로운 주문 엔티티를 생성합니다.
     *
     * @param deliverId 배송 ID
     * @param hubId 허브 ID
     * @param companyId 업체 ID
     * @param orderRequest 주문 요청 사항
     * @param deliveryDeadline 배송 마감일
     * @return 생성된 주문 엔티티
     */
    public Order createOrder(UUID deliverId, UUID hubId, UUID companyId,
                             String orderRequest, LocalDateTime deliveryDeadline) {
        Order order = Order.builder()
                .deliverId(deliverId)
                .hubId(hubId)
                .companyId(companyId)
                .orderRequest(orderRequest)
                .deliveryDeadline(deliveryDeadline)
                .status(OrderStatus.WAITING)
                .totalPrice(0) // 초기값
                .orderProducts(new ArrayList<>())
                .build();

        return order;
    }

    /**
     * 주문에 추가할 상품 엔티티를 생성합니다.
     *
     * @param productId 상품 ID
     * @param quantity 수량
     * @param unitPrice 단가
     * @return 생성된 주문 상품 엔티티
     */
    public OrderProduct createOrderProduct(UUID productId, int quantity, int unitPrice) {
        int totalPrice = quantity * unitPrice;

        return OrderProduct.builder()
                .productId(productId)
                .totalQuantity(quantity)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * 주문의 상태를 업데이트합니다.
     *
     * @param order 대상 주문 엔티티
     * @param newStatus 새로운 주문 상태
     */
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        order.updateStatus(newStatus);
    }

    /**
     * 주문 정보를 업데이트합니다.
     *
     * @param order 대상 주문 엔티티
     * @param deliverId 새 배송 ID
     * @param hubId 새 허브 ID
     * @param companyId 새 업체 ID
     * @param orderRequest 새 주문 요청 사항
     * @param deliveryDeadline 새 배송 마감일
     */
    public void updateOrderInfo(Order order, UUID deliverId, UUID hubId,
                                UUID companyId, String orderRequest,
                                LocalDateTime deliveryDeadline) {
        order.updateOrderInfo(deliverId, hubId, companyId, orderRequest, deliveryDeadline);
    }

    /**
     * 주문 상품을 업데이트합니다.
     *
     * @param order 대상 주문 엔티티
     * @param newProducts 새 주문 상품 목록
     */
    public void updateOrderProducts(Order order, List<OrderProduct> newProducts) {
        // 기존 상품 목록 비우기
        order.getOrderProducts().clear();

        // 새로운 상품 목록 추가
        for (OrderProduct product : newProducts) {
            order.addOrderProduct(product);
        }

        // 총 가격 재계산
        order.calculateTotalPrice();
    }
}