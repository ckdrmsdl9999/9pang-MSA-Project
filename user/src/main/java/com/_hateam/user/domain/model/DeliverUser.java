package com._hateam.user.domain.model;

import com._hateam.common.entity.Timestamped;
import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="p_deliver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverUser extends Timestamped {

    @Id
    @Column(name = "deliver_id")
    private UUID deliverId;

    @Column(name = "hub_id")
    private UUID hubId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "slack_id")
    private String slackId;

    @Column(nullable = false)
    private String name;

    @Column(name = "deliver_type")
    @Enumerated(EnumType.STRING)
    private DeliverType deliverType;

    @Column(name = "contact_number",nullable = false)
    private String contactNumber;

    @Column(name = "rotation_order",nullable = false)
    private Integer rotationOrder;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;



}
