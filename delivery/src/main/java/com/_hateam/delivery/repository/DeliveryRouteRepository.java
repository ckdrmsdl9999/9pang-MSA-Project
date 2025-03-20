package com._hateam.delivery.repository;

import com._hateam.delivery.entity.Delivery;
import com._hateam.delivery.entity.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID> {
    Optional<DeliveryRoute> findByIdAndDeletedAtIsNull(final UUID deliveryId);
    Optional<DeliveryRoute> findByDeliveryAndSequence(final Delivery delivery, final Integer sequence);
}
