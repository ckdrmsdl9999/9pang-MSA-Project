package com._hateam.order.infrastructure.repository;

import com._hateam.order.domain.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaOrderProductRepository extends JpaRepository<OrderProduct, UUID> {

    @Query("SELECT op FROM OrderProduct op WHERE op.order.orderId = :orderId AND op.deletedAt IS NULL")
    List<OrderProduct> findByOrderId(@Param("orderId") UUID orderId);
}