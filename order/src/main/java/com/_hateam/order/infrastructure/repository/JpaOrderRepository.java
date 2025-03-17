package com._hateam.order.infrastructure.repository;

import com._hateam.order.domain.model.Order;
import com._hateam.order.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "(CAST(:companyId AS string) IS NULL OR o.companyId = :companyId) AND " +
            "(LOWER(o.orderRequest) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.deletedAt IS NULL")
    Page<Order> search(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("companyId") UUID companyId,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL")
    Page<Order> findAllActive(Pageable pageable);
}