package com._hateam.deliver_user.domain.model;

import com._hateam.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="p_deliver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverUser extends Timestamped{

    @Id
    @Column(name = "deliver_id")
    UUID deliverId;

    @Column(name = "hub_id")
    UUID hubId;

    @Column(name = "user_id")//fk
    Long userId;

    @Column(name = "slack_id")
    String slackId;

    @Column(nullable = false)
    String name;

    @Column(name = "deliver_type")
    String deliverType;

    @Column(name = "contact_number",nullable = false)
    String contactNumber;

    @Column(name = "rotation_order",nullable = false)
    Integer rotationOrder;

    boolean status;


}


