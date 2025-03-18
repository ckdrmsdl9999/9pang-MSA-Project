package com._hateam.user.infrastructure.repository;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) { return jpaUserRepository.save(user); }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaUserRepository.findById(userId);
    }

    @Override
    public List<User> findAllByDeletedAtIsNull() {
        return jpaUserRepository.findAllByDeletedAtIsNull();
    }

    @Override
    public List<User> findByUserRoles(UserRole role) {
        return jpaUserRepository.findByUserRoles(role);
    }

    @Override
    public List<User> findByNicknameContaining(String nickname) {
        return jpaUserRepository.findByNicknameContaining(nickname);
    }


}
