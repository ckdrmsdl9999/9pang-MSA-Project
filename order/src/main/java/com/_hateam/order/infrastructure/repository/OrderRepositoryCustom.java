package com._hateam.order.infrastructure.repository;

import com._hateam.order.domain.model.Order;
import com._hateam.order.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepositoryCustom {

    // 동적 쿼리를 이용한 주문 검색
    List<Order> searchByDynamicCondition(
            String searchTerm,
            OrderStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            UUID companyId,
            UUID hubId,
            UUID productId,
            int page,
            int size,
            String sort
    );

    // 동적 쿼리를 이용한 검색 결과 개수 조회
    long countByDynamicCondition(
            String searchTerm,
            OrderStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            UUID companyId,
            UUID hubId,
            UUID productId
    );
}