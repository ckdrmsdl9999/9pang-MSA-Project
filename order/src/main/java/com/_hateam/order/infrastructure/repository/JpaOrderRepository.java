package com._hateam.order.infrastructure.repository;

import com._hateam.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {

    // 기본 조회
    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    List<Order> findAllOrdersActive(Pageable pageable);

    // 페이지 객체로 반환
    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL")
    Page<Order> findAllActive(Pageable pageable);
}