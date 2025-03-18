package com._hateam.user.infrastructure.repository;

import com._hateam.user.domain.model.DeliverUser;
import com._hateam.user.domain.repository.DeliverUserRepository;
import com._hateam.user.domain.enums.DeliverType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliverUserRepositoryImpl implements DeliverUserRepository{

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
    public List<DeliverUser> findByHubId(UUID hubId) {
        return jpaDeliverUserRepository.findByHubId(hubId);
    }

    @Override
    public Optional<DeliverUser> findByUser_UserId(Long userId) {
        return jpaDeliverUserRepository.findByUser_UserId(userId);
    }

    @Override
    public List<DeliverUser> findAll() {
        return jpaDeliverUserRepository.findAll();
    }


    @Override
    public List<DeliverUser> findByHubIdAndDeletedAtIsNull(UUID hubId) {
        return jpaDeliverUserRepository.findByHubIdAndDeletedAtIsNull(hubId);
    }

    @Override
    public List<DeliverUser> findByNameContainingAndDeletedAtIsNull(String name) {
        return jpaDeliverUserRepository.findByNameContainingAndDeletedAtIsNull(name);
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

}
