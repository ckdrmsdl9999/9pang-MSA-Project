package com._hateam.user.domain.model;


import com._hateam.common.entity.Timestamped;
import com._hateam.user.domain.enums.UserRole;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Entity
@Table(name="p_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Timestamped {

    @Id
    String userId;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String slack_id;

    @Enumerated(EnumType.STRING)
    UserRole userRoles;

    @Column(nullable = false)
    boolean is_deliver;



}
