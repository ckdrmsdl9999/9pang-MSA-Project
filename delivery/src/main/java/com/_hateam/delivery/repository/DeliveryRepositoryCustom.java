package com._hateam.delivery.repository;

import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.delivery.dto.response.DeliveryResponseDto;
import com._hateam.delivery.entity.DeliveryStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com._hateam.delivery.entity.QDelivery.delivery;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryCustom {
    // todo: impl 적용해보기

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 관리자 배송 전체 조회
     */
    public Page<DeliveryResponseDto> findDeliveryListWithPage(final Pageable pageable) {
        List<DeliveryResponseDto> content = getDeliveryList(pageable);
        long total = getTotalDataCount();

        return new PageImpl<>(content, pageable, total);
    }
    /**
     * 관리자 배송 검색
     */
    public Page<DeliveryResponseDto> searchDeliveryListWithPage(final DeliveryStatus status,
                                                                final String keyword,
                                                                final Pageable pageable) {
        List<DeliveryResponseDto> content = getDeliveryList(pageable, status, keyword);
        long total = getTotalDataCount(status, keyword);

        return new PageImpl<>(content, pageable, total);
    }


    /**
     * 관리자 배송 전체 조회 메서드
     */
    private List<DeliveryResponseDto> getDeliveryList(final Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(DeliveryResponseDto.class,
                        delivery.id,
                        delivery.orderId,
                        delivery.status,
                        delivery.startHubId,
                        delivery.endHubId,
                        delivery.receiverAddress,
                        delivery.receiverName,
                        delivery.receiverSlackId,
                        delivery.deliverId,
                        delivery.deliverSlackId
                ))
                .from(delivery)
                .where(getWhereConditions())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderConditions(pageable.getSort()))
                .fetch();
    }

    /**
     * 관리자 배송 검색 메서드
     */
    private List<DeliveryResponseDto> getDeliveryList(final Pageable pageable,
                                                     final DeliveryStatus status,
                                                     final String keyword) {
        return jpaQueryFactory
                .select(Projections.constructor(DeliveryResponseDto.class,
                        delivery.id,
                        delivery.orderId,
                        delivery.status,
                        delivery.startHubId,
                        delivery.endHubId,
                        delivery.receiverAddress,
                        delivery.receiverName,
                        delivery.receiverSlackId,
                        delivery.deliverId,
                        delivery.deliverSlackId
                ))
                .from(delivery)
                .where(getWhereConditions(status, keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderConditions(pageable.getSort()))
                .fetch();
    }



    /**
     * 전체 데이터 수 조회 admin
     */
    private long getTotalDataCount() {
        return Optional.ofNullable(jpaQueryFactory
                        .select(delivery.count())
                        .from(delivery)
                        .where(getWhereConditions())
                        .fetchOne()
                )
                .orElse(0L);
    }

    /**
     * 전체 데이터 수 조회 admin-search
     */
    private long getTotalDataCount(final DeliveryStatus status, final String keyword) {
        return Optional.ofNullable(jpaQueryFactory
                        .select(delivery.count())
                        .from(delivery)
                        .where(getWhereConditions(status, keyword))
                        .fetchOne()
                )
                .orElse(0L);
    }


    /**
     * 조회 조건 admin
     */
    private BooleanBuilder getWhereConditions() {
        BooleanBuilder builder = new BooleanBuilder();

        return builder.and(delivery.deletedAt.isNull());

    }

    /**
     * 조회 조건 admin search
     */
    private BooleanBuilder getWhereConditions(final DeliveryStatus status, final String keyword) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(delivery.deletedAt.isNull());

        // Check for the keyword and add conditions
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Use OR conditions instead of AND for keyword search
            BooleanBuilder keywordBuilder = new BooleanBuilder();

            // Cast UUID to string at the database level before applying contains
            // For PostgreSQL
            keywordBuilder.or(Expressions.stringTemplate("cast({0} as text)", delivery.id).containsIgnoreCase(keyword));
            keywordBuilder.or(Expressions.stringTemplate("cast({0} as text)", delivery.orderId).containsIgnoreCase(keyword));

            builder.and(keywordBuilder);
        }

        // Status filter
        if (status != null) {
            BooleanExpression statusCondition = delivery.status.eq(status);
            builder.and(statusCondition);
        }

        return builder;
    }

    /**
     * 정렬 조건
     */
    private OrderSpecifier<?> getOrderConditions(final Sort sort) {
        Sort.Order firstOrder = sort.stream().findFirst().orElse(null);

        if (firstOrder == null) {
            return delivery.createdAt.asc();
        }

        String field = firstOrder.getProperty();
        boolean isASC = firstOrder.getDirection().isAscending();
        // 이상한 값을 보내면 asc를 기본으로 해준다. 대소문자 구분없이 사용가능

        switch (field) {
            case "createdAt":
                if (isASC) return delivery.createdAt.asc();
                else return delivery.createdAt.desc();
            case "updatedAt":
                if (isASC) return delivery.updatedAt.asc();
                else return delivery.updatedAt.desc();
            default:
                throw new CustomNotFoundException("존재하지 않는 정렬조건입니다.");
        }
    }
}
