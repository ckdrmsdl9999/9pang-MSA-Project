package com._hateam.user.infrastructure.repository;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.List;
public interface JpaUserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    List<User> findAllByDeletedAtIsNull();

    // 추가: 페이징 및 정렬을 위한 새로운 메서드
    Page<User> findByUsernameContainingAndDeletedAtIsNull(String username, Pageable pageable);

}
