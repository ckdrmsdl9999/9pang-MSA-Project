package com._hateam.user.infrastructure.repository;


import com._hateam.user.domain.model.DeliverUser;
import com._hateam.user.domain.repository.DeliverUserRepository;
import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import lombok.RequiredArgsConstructor;
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
    public List<DeliverUser> findByDeliverType(DeliverType deliverType) {
        return jpaDeliverUserRepository.findByDeliverType(deliverType);
    }

    @Override
    public List<DeliverUser> findByStatus(Status status) {
        return jpaDeliverUserRepository.findByStatus(status);
    }

    @Override
    public List<DeliverUser> findByStatusAndHubId(Status status, UUID hubId) {
        return jpaDeliverUserRepository.findByStatusAndHubId(status, hubId);
    }

    @Override
    public Optional<DeliverUser> findBySlackId(String slackId) {
        return jpaDeliverUserRepository.findBySlackId(slackId);
    }

    @Override
    public List<DeliverUser> findAllByOrderByRotationOrderAsc() {
        return jpaDeliverUserRepository.findAllByOrderByRotationOrderAsc();
    }

    @Override
    public boolean existsByContactNumber(String contactNumber) {
        return jpaDeliverUserRepository.existsByContactNumber(contactNumber);
    }

    @Override
    public List<DeliverUser> findAll() {
        return jpaDeliverUserRepository.findAll();
    }

    @Override
    public void deleteByDeliverId(UUID deliverId) {
        jpaDeliverUserRepository.deleteById(deliverId);
    }
}
