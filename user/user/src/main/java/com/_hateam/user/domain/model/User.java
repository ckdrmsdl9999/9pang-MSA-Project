package com._hateam.user.domain.model;

import com._hateam.common.entity.Timestamped;
import com._hateam.user.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="p_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Timestamped {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String nickname;

    @Column(nullable = false)
    String password;

    @Column(name = "slack_id", nullable = false)
    String slackId;

    @Enumerated(EnumType.STRING)
    UserRole userRoles;

    @Column(name="is_deliver",nullable = false)
    boolean isDeliver;

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private DeliverUser deliverUser;

}
