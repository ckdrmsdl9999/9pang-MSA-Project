package com._hateam.user.infrastructure.security;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;


@Getter
@AllArgsConstructor
public class UserPrincipals implements UserDetails {


    private Long id;
    private String username;
    private String password;
    private UserRole role;

    public static UserPrincipals create(User user) {

        // 단일 역할을 가진 사용자를 처리
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(user.getUserRoles().name()));

        return new UserPrincipals(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getUserRoles()
        );
    }
    // UserDetails 인터페이스의 getAuthorities 메서드 구현 추가
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
