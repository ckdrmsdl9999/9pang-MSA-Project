package com._hateam.delivery.repository;

import com._hateam.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Optional<Delivery> findByIdAndDeletedAtIsNull(final UUID deliveryId);
    boolean existsByOrderIdAndDeletedAtIsNull(final UUID orderId);
}
