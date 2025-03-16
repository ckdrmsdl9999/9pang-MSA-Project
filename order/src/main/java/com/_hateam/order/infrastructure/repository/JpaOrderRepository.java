package com._hateam.order.infrastructure.repository;

import com._hateam.order.domain.model.Order;
import com._hateam.order.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {

    // 1. 기본 조회
    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findAllOrdersActive(Pageable pageable);

    // 2. 단일 조건 쿼리

    // 2.1 검색어만 있는 경우
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    // 2.2 상태만 있는 경우
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = :status AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    // 2.3 회사 ID만 있는 경우
    @Query("SELECT o FROM Order o WHERE " +
            "o.companyId = :companyId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);

    // 2.4 허브 ID만 있는 경우
    @Query("SELECT o FROM Order o WHERE " +
            "o.hubId = :hubId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByHubId(@Param("hubId") UUID hubId, Pageable pageable);

    // 2.5 날짜 범위만 있는 경우
    @Query("SELECT o FROM Order o WHERE " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 3. 2개 조건 조합 쿼리

    // 3.1 검색어 + 상태
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.status = :status AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndStatus(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            Pageable pageable);

    // 3.2 검색어 + 회사 ID
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.companyId = :companyId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndCompanyId(
            @Param("searchTerm") String searchTerm,
            @Param("companyId") UUID companyId,
            Pageable pageable);

    // 3.3 검색어 + 허브 ID
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.hubId = :hubId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndHubId(
            @Param("searchTerm") String searchTerm,
            @Param("hubId") UUID hubId,
            Pageable pageable);

    // 3.4 검색어 + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 3.5 상태 + 회사 ID
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = :status AND " +
            "o.companyId = :companyId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStatusAndCompanyId(
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            Pageable pageable);

    // 3.6 상태 + 허브 ID
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = :status AND " +
            "o.hubId = :hubId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStatusAndHubId(
            @Param("status") OrderStatus status,
            @Param("hubId") UUID hubId,
            Pageable pageable);

    // 3.7 상태 + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = :status AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStatusAndDateRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 3.8 회사 ID + 허브 ID
    @Query("SELECT o FROM Order o WHERE " +
            "o.companyId = :companyId AND " +
            "o.hubId = :hubId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByCompanyIdAndHubId(
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            Pageable pageable);

    // 3.9 회사 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "o.companyId = :companyId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByCompanyIdAndDateRange(
            @Param("companyId") UUID companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 3.10 허브 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "o.hubId = :hubId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByHubIdAndDateRange(
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 4. 3개 조건 조합 쿼리

    // 4.1 검색어 + 상태 + 회사 ID
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.status = :status AND " +
            "o.companyId = :companyId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndStatusAndCompanyId(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            Pageable pageable);

    // 4.2 검색어 + 상태 + 허브 ID
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.status = :status AND " +
            "o.hubId = :hubId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndStatusAndHubId(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("hubId") UUID hubId,
            Pageable pageable);

    // 4.3 검색어 + 상태 + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.status = :status AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndStatusAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 4.4 검색어 + 회사 ID + 허브 ID
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.companyId = :companyId AND " +
            "o.hubId = :hubId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndCompanyIdAndHubId(
            @Param("searchTerm") String searchTerm,
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            Pageable pageable);

    // 4.5 검색어 + 회사 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.companyId = :companyId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndCompanyIdAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("companyId") UUID companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 4.6 검색어 + 허브 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.hubId = :hubId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndHubIdAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 4.7 상태 + 회사 ID + 허브 ID
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = :status AND " +
            "o.companyId = :companyId AND " +
            "o.hubId = :hubId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStatusAndCompanyIdAndHubId(
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            Pageable pageable);

    // 4.8 상태 + 회사 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = :status AND " +
            "o.companyId = :companyId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStatusAndCompanyIdAndDateRange(
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 4.9 상태 + 허브 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = :status AND " +
            "o.hubId = :hubId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStatusAndHubIdAndDateRange(
            @Param("status") OrderStatus status,
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 4.10 회사 ID + 허브 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "o.companyId = :companyId AND " +
            "o.hubId = :hubId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByCompanyIdAndHubIdAndDateRange(
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 5. 4개 조건 조합 쿼리

    // 5.1 검색어 + 상태 + 회사 ID + 허브 ID
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.status = :status AND " +
            "o.companyId = :companyId AND " +
            "o.hubId = :hubId AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndStatusAndCompanyIdAndHubId(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            Pageable pageable);

    // 5.2 검색어 + 상태 + 회사 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.status = :status AND " +
            "o.companyId = :companyId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndStatusAndCompanyIdAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 5.3 검색어 + 상태 + 허브 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.status = :status AND " +
            "o.hubId = :hubId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndStatusAndHubIdAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 5.4 검색어 + 회사 ID + 허브 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.companyId = :companyId AND " +
            "o.hubId = :hubId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findBySearchTermAndCompanyIdAndHubIdAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 5.5 상태 + 회사 ID + 허브 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = :status AND " +
            "o.companyId = :companyId AND " +
            "o.hubId = :hubId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByStatusAndCompanyIdAndHubIdAndDateRange(
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 6. 모든 조건 조합 쿼리 (5개 조건)

    // 6.1 검색어 + 상태 + 회사 ID + 허브 ID + 날짜 범위
    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "o.status = :status AND " +
            "o.companyId = :companyId AND " +
            "o.hubId = :hubId AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByAllCriteria(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL")
    Page<Order> findAllActive(Pageable pageable);

    // 카운트 쿼리
    @Query("SELECT COUNT(o) FROM Order o WHERE " +
            "(LOWER(COALESCE(o.orderRequest, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:companyId IS NULL OR o.companyId = :companyId) AND " +
            "(:hubId IS NULL OR o.hubId = :hubId) AND " +
            "(:startDate IS NULL OR o.deliveryDeadline >= :startDate) AND " +
            "(:endDate IS NULL OR o.deliveryDeadline <= :endDate) AND " +
            "o.deletedAt IS NULL")
    long countByCriteria(
            @Param("searchTerm") String searchTerm,
            @Param("status") OrderStatus status,
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}