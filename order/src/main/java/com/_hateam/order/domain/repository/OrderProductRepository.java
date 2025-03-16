package com._hateam.order.domain.repository;

import com._hateam.order.domain.model.OrderProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderProductRepository {
    OrderProduct save(OrderProduct orderProduct);

    Optional<OrderProduct> findById(UUID orderProductId);

    List<OrderProduct> findByOrderId(UUID orderId);

    void delete(OrderProduct orderProduct);
}