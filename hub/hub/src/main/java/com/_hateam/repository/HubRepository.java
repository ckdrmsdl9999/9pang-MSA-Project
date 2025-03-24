package com._hateam.repository;

import com._hateam.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {
    Optional<Hub> findByNameAndDeletedAtIsNull(String name);
    Optional<Hub> findByName(String sourceName);

}
