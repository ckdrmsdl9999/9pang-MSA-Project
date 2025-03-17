package com._hateam.user.infrastructure.repository;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.List;
public interface JpaUserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

//    Collection<User>  findAllByDeletedAtIsNull();

    List<User> findAllByDeletedAtIsNull();

    List<User> findByUserRoles(UserRole role);

    List<User> findByNicknameContaining(String nickname);

   // List<User> findByApproved(boolean approved);

}
