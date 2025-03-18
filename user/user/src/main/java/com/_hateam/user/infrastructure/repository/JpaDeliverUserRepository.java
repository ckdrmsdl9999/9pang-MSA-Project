package com._hateam.user.infrastructure.repository;


import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import com._hateam.user.domain.model.DeliverUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDeliverUserRepository  extends JpaRepository<DeliverUser, UUID> {

    Optional<DeliverUser> findByDeliverId(UUID deliverId);

    List<DeliverUser> findByHubId(UUID hubId);

    Optional<DeliverUser> findByUser_UserId(Long userId);

    List<DeliverUser> findByDeliverType(DeliverType deliverType);

    List<DeliverUser> findByStatus(Status status);

    List<DeliverUser> findByStatusAndHubId(Status status, UUID hubId);

    Optional<DeliverUser> findBySlackId(String slackId);

    List<DeliverUser> findAllByOrderByRotationOrderAsc();

    boolean existsByContactNumber(String contactNumber);

    List<DeliverUser> findByHubIdAndDeletedAtIsNull(UUID hubId);

    List<DeliverUser> findByNameContainingAndDeletedAtIsNull(String name);

    }
