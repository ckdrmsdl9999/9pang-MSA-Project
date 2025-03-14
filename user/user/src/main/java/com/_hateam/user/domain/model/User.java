package com._hateam.user.domain.model;

import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Entity
@Table(name="p_user")
public class User {

    @Id
    String userId;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String slack_id;

//    @Enumerated(EnumType.STRING)
//    UserRoles userRoles;

    @Column(nullable = false)
    boolean is_deliver;



}
