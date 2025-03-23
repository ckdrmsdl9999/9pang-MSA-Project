package com._hateam.user.infrastructure.repository;

import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.model.DeliverUser;
import com._hateam.user.domain.repository.DeliverUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com._hateam.user.domain.enums.Status;
@Repository
@RequiredArgsConstructor
public class DeliverUserRepositoryImpl implements DeliverUserRepository {

    private final JpaDeliverUserRepository jpaDeliverUserRepository;

    @Override
    public DeliverUser save(DeliverUser deliverUser) {
        return jpaDeliverUserRepository.save(deliverUser);
    }

    @Override
    public Optional<DeliverUser> findByDeliverId(UUID deliverId) {
        return jpaDeliverUserRepository.findByDeliverId(deliverId);
    }

    @Override
    public Optional<DeliverUser> findByUser_UserId(Long userId) {
        return jpaDeliverUserRepository.findByUser_UserId(userId);
    }

    @Override
    public List<DeliverUser> findByHubIdAndDeletedAtIsNull(UUID hubId) {
        return jpaDeliverUserRepository.findByHubIdAndDeletedAtIsNull(hubId);
    }

    @Override
    public List<DeliverUser> findByDeletedAtIsNull() {
        return jpaDeliverUserRepository.findByDeletedAtIsNull();
    }

    @Override
    public Page<DeliverUser> findByNameContainingAndDeletedAtIsNull(String name, Pageable pageable) {
        return jpaDeliverUserRepository.findByNameContainingAndDeletedAtIsNull(name, pageable);
    }

    @Override
    public Page<DeliverUser> findByNameContainingAndHubIdAndDeletedAtIsNull(String name, UUID hubId, Pageable pageable) {
        return jpaDeliverUserRepository.findByNameContainingAndHubIdAndDeletedAtIsNull(name, hubId, pageable);
    }

    @Override
    public List<DeliverUser> findByDeliverTypeAndDeletedAtIsNull(DeliverType deliverType) {
        return jpaDeliverUserRepository.findByDeliverTypeAndDeletedAtIsNull(deliverType);
    }

    @Override
    public List<DeliverUser> findByStatusAndDeliverTypeOrderByRotationOrderAsc
            (Status status, DeliverType deliverType) {
        return jpaDeliverUserRepository.findByStatusAndDeliverTypeOrderByRotationOrderAsc
                (status,deliverType);
    }

    @Override
    public List<DeliverUser> findByStatusAndDeliverTypeAndHubIdOrderByRotationOrderAsc
            (Status status, DeliverType deliverType, UUID hubId) {
        return jpaDeliverUserRepository.findByStatusAndDeliverTypeAndHubIdOrderByRotationOrderAsc(
                status, deliverType, hubId);
    }
 ///////////



 // 추가: (deliverType, hubId)별로 rotationOrder 중 가장 큰 값 조회
 @Override
 public Integer findMaxRotationOrder(DeliverType deliverType, UUID hubId) {
     return jpaDeliverUserRepository.findMaxRotationOrder(deliverType, hubId);
 }




}



