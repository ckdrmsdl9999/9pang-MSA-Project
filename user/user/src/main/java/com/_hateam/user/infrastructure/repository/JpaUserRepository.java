package com._hateam.user.infrastructure.repository;

import com._hateam.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User,String> {

    Optional<User> findByUsername(String username);

    Collection<User>  findAllByDeletedAtIsNull();

}
