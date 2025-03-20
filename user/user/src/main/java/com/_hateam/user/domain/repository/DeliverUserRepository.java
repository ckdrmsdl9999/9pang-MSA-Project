package com._hateam.user.domain.repository;

import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.model.DeliverUser;
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


    Optional<DeliverUser> findByUser_UserId(Long userId);

    List<DeliverUser> findByHubIdAndDeletedAtIsNull(UUID hubId);

    List<DeliverUser> findByDeletedAtIsNull();
    // 페이징 메소드 추가
    Page<DeliverUser> findByNameContainingAndDeletedAtIsNull(String name, Pageable pageable);

    Page<DeliverUser> findByNameContainingAndHubIdAndDeletedAtIsNull(String name, UUID hubId, Pageable pageable);

    List<DeliverUser> findByDeliverTypeAndDeletedAtIsNull(DeliverType deliverType);

}
