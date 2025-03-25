package com._hateam.repository;

import com._hateam.entity.Hub;
import com._hateam.entity.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {
    HubRoute findBySourceHubAndDestinationHubAndDeletedAtIsNull(Hub sourceHub, Hub destinationHub);
}
