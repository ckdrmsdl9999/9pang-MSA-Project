package com._hateam.user.infrastructure.repository;


import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import com._hateam.user.domain.model.DeliverUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDeliverUserRepository  extends JpaRepository<DeliverUser, UUID> {

    Optional<DeliverUser> findByDeliverId(UUID deliverId);

    List<DeliverUser> findByHubId(UUID hubId);

    Optional<DeliverUser> findByUser_UserId(Long userId);

    List<DeliverUser> findByDeliverType(DeliverType deliverType);

    List<DeliverUser> findAllByOrderByRotationOrderAsc();

    boolean existsByContactNumber(String contactNumber);

    List<DeliverUser> findByHubIdAndDeletedAtIsNull(UUID hubId);

    List<DeliverUser> findByNameContainingAndDeletedAtIsNull(String name);

    List<DeliverUser> findByDeletedAtIsNull();

    Page<DeliverUser> findByNameContainingAndDeletedAtIsNull(String name, Pageable pageable);

    Page<DeliverUser> findByNameContainingAndHubIdAndDeletedAtIsNull(String name, UUID hubId, Pageable pageable);

    List<DeliverUser> findByDeliverTypeAndDeletedAtIsNull(DeliverType deliverType);
    //user도메인


}