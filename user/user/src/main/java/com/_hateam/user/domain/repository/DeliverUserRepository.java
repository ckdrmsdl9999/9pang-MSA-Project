package com._hateam.user.domain.repository;

import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.DeliverUser;
import com._hateam.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<DeliverUser> findAll();

    List<DeliverUser> findByHubIdAndDeletedAtIsNull(UUID hubId);

    List<DeliverUser> findByNameContainingAndDeletedAtIsNull(String name);

    List<DeliverUser> findByDeletedAtIsNull();
    // 페이징 메소드 추가
    Page<DeliverUser> findByNameContainingAndDeletedAtIsNull(String name, Pageable pageable);

    Page<DeliverUser> findByNameContainingAndHubIdAndDeletedAtIsNull(String name, UUID hubId, Pageable pageable);

}
