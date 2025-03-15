package com._hateam.repository;

import com._hateam.entity.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {

    Optional<HubRoute> findByNameAndDeletedAtIsNull(String name);

}
