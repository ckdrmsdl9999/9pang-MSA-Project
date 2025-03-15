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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String nickname;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String slack_id;

    @Enumerated(EnumType.STRING)
    UserRole userRoles;

    @Column
    boolean is_deliver;



}
