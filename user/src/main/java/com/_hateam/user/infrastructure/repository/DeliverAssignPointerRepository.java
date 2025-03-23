package com._hateam.user.infrastructure.repository;
import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.model.DeliverAssignPointer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
public interface DeliverAssignPointerRepository extends JpaRepository<DeliverAssignPointer, Long> {

    // deliverType + hubId 조합으로 Pointer 찾기
    Optional<DeliverAssignPointer> findByDeliverTypeAndHubId(DeliverType deliverType, UUID hubId);

}
