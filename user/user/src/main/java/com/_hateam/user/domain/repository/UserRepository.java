package com._hateam.user.domain.repository;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {

    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long userId);

    List<User> findAllByDeletedAtIsNull();

    // 페이징 및 정렬을 위한 새로운 메서드
    Page<User> findByUsernameContainingAndDeletedAtIsNull(String username, Pageable pageable);


}
