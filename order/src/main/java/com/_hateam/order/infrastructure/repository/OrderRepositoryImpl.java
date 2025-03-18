package com._hateam.order.infrastructure.repository;

import com._hateam.order.domain.model.Order;
import com._hateam.order.domain.model.OrderStatus;
import com._hateam.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaOrderRepository.findById(orderId)
                .filter(order -> order.getDeletedAt() == null);
    }

    @Override
    public List<Order> findAll(int page, int size, String sort) {
        int limitedSize = limitSize(size);

        Sort.Direction direction = sort.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page - 1, limitedSize,
                Sort.by(direction, "createdAt"));

        Page<Order> orderPage = jpaOrderRepository.findAllActive(pageable);
        return orderPage.getContent();
    }

    @Override
    public List<Order> search(String searchTerm, OrderStatus status,
                              LocalDateTime startDate, LocalDateTime endDate,
                              UUID companyId, UUID hubId,
                              int page, int size, String sort) {
        int limitedSize = limitSize(size);

        try {
            return jpaOrderRepository.searchByDynamicCondition(
                    searchTerm, status, startDate, endDate, companyId, hubId,
                    page, limitedSize, sort
            );
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public long countSearchResults(String searchTerm, OrderStatus status,
                                   LocalDateTime startDate, LocalDateTime endDate,
                                   UUID companyId, UUID hubId) {
        try {
            return jpaOrderRepository.countByDynamicCondition(
                    searchTerm, status, startDate, endDate, companyId, hubId
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void delete(Order order) {
        order.delete("system");
        jpaOrderRepository.save(order);
    }

    @Override
    public boolean existsById(UUID orderId) {
        return jpaOrderRepository.existsById(orderId);
    }

    // 페이지 크기 제한 메서드
    private int limitSize(int size) {
        if (size <= 0) return 10; // 기본값
        else if (size <= 10) return 10;
        else if (size <= 30) return 30;
        else return 50; // 최대 50
    }
}