package com._hateam.user.domain.repository;

import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.DeliverUser;
import com._hateam.user.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliverUserRepository {

    DeliverUser save(DeliverUser deliverUser);

    Optional<DeliverUser> findByDeliverId(UUID deliverId);

    List<DeliverUser> findByHubId(UUID hubId);

    Optional<DeliverUser> findByUser_UserId(Long userId);

    List<DeliverUser> findByDeliverType(DeliverType deliverType);

    List<DeliverUser> findByStatus(Status status);

    List<DeliverUser> findByStatusAndHubId(Status status, UUID hubId);

    Optional<DeliverUser> findBySlackId(String slackId);

    List<DeliverUser> findAllByOrderByRotationOrderAsc();

    boolean existsByContactNumber(String contactNumber);

    List<DeliverUser> findAll();

    void deleteByDeliverId(UUID deliverId);

}
