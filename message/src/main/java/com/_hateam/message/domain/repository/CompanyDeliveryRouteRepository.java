package com._hateam.message.domain.repository;

import com._hateam.message.domain.model.CompanyDeliveryRoute;
import com._hateam.message.domain.model.CompanyDeliveryRoute.DeliveryRouteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyDeliveryRouteRepository extends JpaRepository<CompanyDeliveryRoute, UUID> {

    // 특정 배송담당자의 오늘 배송해야 할 경로 목록 조회
    @Query("SELECT r FROM CompanyDeliveryRoute r " +
            "WHERE r.deliverId = :deliverId AND r.status IN (:waitingStatus, :movingStatus) " +
            "AND r.deletedAt IS NULL " +
            "ORDER BY r.deliveryOrder ASC")
    List<CompanyDeliveryRoute> findTodayDeliveryRoutesByDeliverId(
            @Param("deliverId") UUID deliverId,
            @Param("waitingStatus") DeliveryRouteStatus waitingStatus,
            @Param("movingStatus") DeliveryRouteStatus movingStatus);

    default List<CompanyDeliveryRoute> findTodayDeliveryRoutesByDeliverId(UUID deliverId) {
        return findTodayDeliveryRoutesByDeliverId(
                deliverId,
                DeliveryRouteStatus.WAITING,
                DeliveryRouteStatus.MOVING_TO_COMPANY
        );
    }

    // 특정 허브에 소속된 배송담당자들의 오늘 배송해야 할 경로 목록 조회
    @Query("SELECT r FROM CompanyDeliveryRoute r " +
            "WHERE r.startHubId = :hubId AND r.status IN (:waitingStatus, :movingStatus) " +
            "AND r.deletedAt IS NULL " +
            "ORDER BY r.deliverId, r.deliveryOrder ASC")
    List<CompanyDeliveryRoute> findTodayDeliveryRoutesByHubId(
            @Param("hubId") UUID hubId,
            @Param("waitingStatus") DeliveryRouteStatus waitingStatus,
            @Param("movingStatus") DeliveryRouteStatus movingStatus);

    default List<CompanyDeliveryRoute> findTodayDeliveryRoutesByHubId(UUID hubId) {
        return findTodayDeliveryRoutesByHubId(
                hubId,
                DeliveryRouteStatus.WAITING,
                DeliveryRouteStatus.MOVING_TO_COMPANY
        );
    }
}