package com._hateam.user.application.dto;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeignVerifyResDto {

    // User 정보
    private Long userId;
    private String password;
    private String userName;
    private UserRole userRole;


    public static FeignVerifyResDto from(User user) {

        return FeignVerifyResDto.builder()
                .userId(user.getUserId())
                .password(user.getPassword())
                .userName(user.getUsername())
                .userRole(user.getUserRoles())
                .build();

    }
}
