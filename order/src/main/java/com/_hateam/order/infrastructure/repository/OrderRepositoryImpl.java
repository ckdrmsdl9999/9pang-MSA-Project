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

        Sort.Direction direction = sort.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page - 1, limitedSize,
                Sort.by(direction, "createdAt"));

        try {
            boolean hasSearchTerm = !isEmpty(searchTerm);
            boolean hasStatus = status != null;
            boolean hasCompanyId = companyId != null;
            boolean hasHubId = hubId != null;
            boolean hasStartDate = startDate != null;
            boolean hasEndDate = endDate != null;
            boolean hasDateRange = hasStartDate || hasEndDate;

            // 1. 모든 조건이 있는 경우
            if (hasSearchTerm && hasStatus && hasCompanyId && hasHubId && hasDateRange) {
                return jpaOrderRepository.findByAllCriteria(
                        searchTerm, status, companyId, hubId, startDate, endDate, pageable);
            }

            // 2. 4개 조건 조합

            // 2.1 검색어 + 상태 + 회사 ID + 허브 ID
            if (hasSearchTerm && hasStatus && hasCompanyId && hasHubId && !hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndStatusAndCompanyIdAndHubId(
                        searchTerm, status, companyId, hubId, pageable);
            }

            // 2.2 검색어 + 상태 + 회사 ID + 날짜 범위
            if (hasSearchTerm && hasStatus && hasCompanyId && !hasHubId && hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndStatusAndCompanyIdAndDateRange(
                        searchTerm, status, companyId, startDate, endDate, pageable);
            }

            // 2.3 검색어 + 상태 + 허브 ID + 날짜 범위
            if (hasSearchTerm && hasStatus && !hasCompanyId && hasHubId && hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndStatusAndHubIdAndDateRange(
                        searchTerm, status, hubId, startDate, endDate, pageable);
            }

            // 2.4 검색어 + 회사 ID + 허브 ID + 날짜 범위
            if (hasSearchTerm && !hasStatus && hasCompanyId && hasHubId && hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndCompanyIdAndHubIdAndDateRange(
                        searchTerm, companyId, hubId, startDate, endDate, pageable);
            }

            // 2.5 상태 + 회사 ID + 허브 ID + 날짜 범위
            if (!hasSearchTerm && hasStatus && hasCompanyId && hasHubId && hasDateRange) {
                return jpaOrderRepository.findByStatusAndCompanyIdAndHubIdAndDateRange(
                        status, companyId, hubId, startDate, endDate, pageable);
            }

            // 3. 3개 조건 조합

            // 3.1 검색어 + 상태 + 회사 ID
            if (hasSearchTerm && hasStatus && hasCompanyId && !hasHubId && !hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndStatusAndCompanyId(
                        searchTerm, status, companyId, pageable);
            }

            // 3.2 검색어 + 상태 + 허브 ID
            if (hasSearchTerm && hasStatus && !hasCompanyId && hasHubId && !hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndStatusAndHubId(
                        searchTerm, status, hubId, pageable);
            }

            // 3.3 검색어 + 상태 + 날짜 범위
            if (hasSearchTerm && hasStatus && !hasCompanyId && !hasHubId && hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndStatusAndDateRange(
                        searchTerm, status, startDate, endDate, pageable);
            }

            // 3.4 검색어 + 회사 ID + 허브 ID
            if (hasSearchTerm && !hasStatus && hasCompanyId && hasHubId && !hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndCompanyIdAndHubId(
                        searchTerm, companyId, hubId, pageable);
            }

            // 3.5 검색어 + 회사 ID + 날짜 범위
            if (hasSearchTerm && !hasStatus && hasCompanyId && !hasHubId && hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndCompanyIdAndDateRange(
                        searchTerm, companyId, startDate, endDate, pageable);
            }

            // 3.6 검색어 + 허브 ID + 날짜 범위
            if (hasSearchTerm && !hasStatus && !hasCompanyId && hasHubId && hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndHubIdAndDateRange(
                        searchTerm, hubId, startDate, endDate, pageable);
            }

            // 3.7 상태 + 회사 ID + 허브 ID
            if (!hasSearchTerm && hasStatus && hasCompanyId && hasHubId && !hasDateRange) {
                return jpaOrderRepository.findByStatusAndCompanyIdAndHubId(
                        status, companyId, hubId, pageable);
            }

            // 3.8 상태 + 회사 ID + 날짜 범위
            if (!hasSearchTerm && hasStatus && hasCompanyId && !hasHubId && hasDateRange) {
                return jpaOrderRepository.findByStatusAndCompanyIdAndDateRange(
                        status, companyId, startDate, endDate, pageable);
            }

            // 3.9 상태 + 허브 ID + 날짜 범위
            if (!hasSearchTerm && hasStatus && !hasCompanyId && hasHubId && hasDateRange) {
                return jpaOrderRepository.findByStatusAndHubIdAndDateRange(
                        status, hubId, startDate, endDate, pageable);
            }

            // 3.10 회사 ID + 허브 ID + 날짜 범위
            if (!hasSearchTerm && !hasStatus && hasCompanyId && hasHubId && hasDateRange) {
                return jpaOrderRepository.findByCompanyIdAndHubIdAndDateRange(
                        companyId, hubId, startDate, endDate, pageable);
            }

            // 4. 2개 조건 조합

            // 4.1 검색어 + 상태
            if (hasSearchTerm && hasStatus && !hasCompanyId && !hasHubId && !hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndStatus(
                        searchTerm, status, pageable);
            }

            // 4.2 검색어 + 회사 ID
            if (hasSearchTerm && !hasStatus && hasCompanyId && !hasHubId && !hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndCompanyId(
                        searchTerm, companyId, pageable);
            }

            // 4.3 검색어 + 허브 ID
            if (hasSearchTerm && !hasStatus && !hasCompanyId && hasHubId && !hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndHubId(
                        searchTerm, hubId, pageable);
            }

            // 4.4 검색어 + 날짜 범위
            if (hasSearchTerm && !hasStatus && !hasCompanyId && !hasHubId && hasDateRange) {
                return jpaOrderRepository.findBySearchTermAndDateRange(
                        searchTerm, startDate, endDate, pageable);
            }

            // 4.5 상태 + 회사 ID
            if (!hasSearchTerm && hasStatus && hasCompanyId && !hasHubId && !hasDateRange) {
                return jpaOrderRepository.findByStatusAndCompanyId(
                        status, companyId, pageable);
            }

            // 4.6 상태 + 허브 ID
            if (!hasSearchTerm && hasStatus && !hasCompanyId && hasHubId && !hasDateRange) {
                return jpaOrderRepository.findByStatusAndHubId(
                        status, hubId, pageable);
            }

            // 4.7 상태 + 날짜 범위
            if (!hasSearchTerm && hasStatus && !hasCompanyId && !hasHubId && hasDateRange) {
                return jpaOrderRepository.findByStatusAndDateRange(
                        status, startDate, endDate, pageable);
            }

            // 4.8 회사 ID + 허브 ID
            if (!hasSearchTerm && !hasStatus && hasCompanyId && hasHubId && !hasDateRange) {
                return jpaOrderRepository.findByCompanyIdAndHubId(
                        companyId, hubId, pageable);
            }

            // 4.9 회사 ID + 날짜 범위
            if (!hasSearchTerm && !hasStatus && hasCompanyId && !hasHubId && hasDateRange) {
                return jpaOrderRepository.findByCompanyIdAndDateRange(
                        companyId, startDate, endDate, pageable);
            }

            // 4.10 허브 ID + 날짜 범위
            if (!hasSearchTerm && !hasStatus && !hasCompanyId && hasHubId && hasDateRange) {
                return jpaOrderRepository.findByHubIdAndDateRange(
                        hubId, startDate, endDate, pageable);
            }

            // 5. 단일 조건

            // 5.1 검색어만 있는 경우
            if (hasSearchTerm && !hasStatus && !hasCompanyId && !hasHubId && !hasDateRange) {
                return jpaOrderRepository.findBySearchTerm(searchTerm, pageable);
            }

            // 5.2 상태만 있는 경우
            if (!hasSearchTerm && hasStatus && !hasCompanyId && !hasHubId && !hasDateRange) {
                return jpaOrderRepository.findByStatus(status, pageable);
            }

            // 5.3 회사 ID만 있는 경우
            if (!hasSearchTerm && !hasStatus && hasCompanyId && !hasHubId && !hasDateRange) {
                return jpaOrderRepository.findByCompanyId(companyId, pageable);
            }

            // 5.4 허브 ID만 있는 경우
            if (!hasSearchTerm && !hasStatus && !hasCompanyId && hasHubId && !hasDateRange) {
                return jpaOrderRepository.findByHubId(hubId, pageable);
            }

            // 5.5 날짜 범위만 있는 경우
            if (!hasSearchTerm && !hasStatus && !hasCompanyId && !hasHubId && hasDateRange) {
                return jpaOrderRepository.findByDateRange(startDate, endDate, pageable);
            }

            // 6. 기본 조회
            return jpaOrderRepository.findAllOrdersActive(pageable);

        } catch (Exception e) {
            return List.of();
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

    @Override
    public long countSearchResults(String searchTerm, OrderStatus status,
                                   LocalDateTime startDate, LocalDateTime endDate,
                                   UUID companyId, UUID hubId) {
        try {
            return jpaOrderRepository.countByCriteria(
                    isEmpty(searchTerm) ? null : searchTerm,
                    status,
                    companyId,
                    hubId,
                    startDate,
                    endDate
            );
        } catch (Exception e) {
            return 0;
        }
    }

    // 페이지 크기 제한 메서드
    private int limitSize(int size) {
        if (size <= 0) return 10; // 기본값
        else if (size <= 10) return 10;
        else if (size <= 30) return 30;
        else return 50; // 최대 50
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}