package com._hateam.order.infrastructure.repository;

import com._hateam.order.domain.model.Order;
import com._hateam.order.domain.model.OrderStatus;
import com._hateam.order.domain.model.QOrder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 검색어 조건 생성
    private BooleanExpression searchTextContains(QOrder order, String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return null;
        }

        return order.orderRequest.containsIgnoreCase(searchTerm)
                .or(Expressions.stringTemplate("CAST({0} AS string)", order.orderId).contains(searchTerm));
    }

    // 주문 상태 조건 생성
    private BooleanExpression statusEq(QOrder order, OrderStatus status) {
        return status != null ? order.status.eq(status) : null;
    }

    // 회사 ID 조건 생성
    private BooleanExpression companyIdEq(QOrder order, UUID companyId) {
        return companyId != null ? order.companyId.eq(companyId) : null;
    }

    // 허브 ID 조건 생성
    private BooleanExpression hubIdEq(QOrder order, UUID hubId) {
        return hubId != null ? order.hubId.eq(hubId) : null;
    }

    // 날짜 범위 조건 생성
    private BooleanExpression betweenDeliveryDate(QOrder order, LocalDateTime startDate, LocalDateTime endDate) {

        if (startDate != null && endDate != null) {
            return order.deliveryDeadline.between(startDate, endDate);
        } else if (startDate != null) {
            return order.deliveryDeadline.goe(startDate);
        } else if (endDate != null) {
            return order.deliveryDeadline.loe(endDate);
        }
        return null;
    }

    // 삭제되지 않은 데이터 조건 생성
    private BooleanExpression isNotDeleted(QOrder order) {
        return order.deletedAt.isNull();
    }

    @Override
    public List<Order> searchByDynamicCondition(
            String searchTerm,
            OrderStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            UUID companyId,
            UUID hubId,
            int page,
            int size,
            String sort) {

        QOrder order = QOrder.order;

        OrderSpecifier<?> orderSpecifier;
        if (sort.equalsIgnoreCase("asc")) {
            orderSpecifier = new OrderSpecifier<>(com.querydsl.core.types.Order.ASC, order.createdAt);
        } else {
            orderSpecifier = new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, order.createdAt);
        }

        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 데이터만 조회
        builder.and(isNotDeleted(order));

        // 검색어 조건 추가
        BooleanExpression searchCondition = searchTextContains(order, searchTerm);
        if (searchCondition != null) {
            builder.and(searchCondition);
        }

        // 상태 조건 추가
        BooleanExpression statusCondition = statusEq(order, status);
        if (statusCondition != null) {
            builder.and(statusCondition);
        }

        // 회사 ID 조건 추가
        BooleanExpression companyCondition = companyIdEq(order, companyId);
        if (companyCondition != null) {
            builder.and(companyCondition);
        }

        // 허브 ID 조건 추가
        BooleanExpression hubCondition = hubIdEq(order, hubId);
        if (hubCondition != null) {
            builder.and(hubCondition);
        }

        // 날짜 조건 추가
        BooleanExpression dateCondition = betweenDeliveryDate(order, startDate, endDate);
        if (dateCondition != null) {
            builder.and(dateCondition);
        }

        // 페이지네이션 적용 및 쿼리 실행
        List<Order> results = queryFactory
                .selectFrom(order)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset((long) (page - 1) * size)
                .limit(size)
                .fetch();

        return results;
    }

    @Override
    public long countByDynamicCondition(
            String searchTerm,
            OrderStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            UUID companyId,
            UUID hubId) {

        QOrder order = QOrder.order;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(isNotDeleted(order));

        BooleanExpression searchCondition = searchTextContains(order, searchTerm);
        if (searchCondition != null) builder.and(searchCondition);

        BooleanExpression statusCondition = statusEq(order, status);
        if (statusCondition != null) builder.and(statusCondition);

        BooleanExpression companyCondition = companyIdEq(order, companyId);
        if (companyCondition != null) builder.and(companyCondition);

        BooleanExpression hubCondition = hubIdEq(order, hubId);
        if (hubCondition != null) builder.and(hubCondition);

        BooleanExpression dateCondition = betweenDeliveryDate(order, startDate, endDate);
        if (dateCondition != null) builder.and(dateCondition);

        return queryFactory
                .selectFrom(order)
                .where(builder)
                .fetchCount();
    }
}