package com._hateam.order.infrastructure.repository;

import com._hateam.order.domain.model.OrderProduct;
import com._hateam.order.domain.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {

    private final JpaOrderProductRepository jpaOrderProductRepository;

    @Override
    public OrderProduct save(OrderProduct orderProduct) {
        return jpaOrderProductRepository.save(orderProduct);
    }

    @Override
    public Optional<OrderProduct> findById(UUID orderProductId) {
        return jpaOrderProductRepository.findById(orderProductId)
                .filter(orderProduct -> orderProduct.getDeletedAt() == null);
    }

    @Override
    public List<OrderProduct> findByOrderId(UUID orderId) {
        return jpaOrderProductRepository.findByOrderId(orderId);
    }

    @Override
    public void delete(OrderProduct orderProduct) {
        orderProduct.delete("system");
        jpaOrderProductRepository.save(orderProduct);
    }
}