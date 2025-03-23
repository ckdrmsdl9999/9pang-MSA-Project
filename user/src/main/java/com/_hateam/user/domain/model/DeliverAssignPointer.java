package com._hateam.user.domain.model;

import com._hateam.user.domain.enums.DeliverType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "deliver_assign_pointer",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"deliverType", "hubId"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverAssignPointer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "deliverType")
    private DeliverType deliverType;

    @Column(name = "hubId")
    private UUID hubId;  // 허브배송이면 null, 업체배송이면 실제 허브 UUID

    // 마지막으로 배정된 rotationOrder
    private Integer lastAssignedRotationOrder;
}
