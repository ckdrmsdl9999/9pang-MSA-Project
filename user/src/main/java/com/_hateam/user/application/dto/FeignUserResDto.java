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
public class FeignUserResDto {
    // User 정보
    private Long userId;
    private String username;
    private String slackId;
    private UserRole userRoles;
    private boolean isDeliver;

    public static FeignUserResDto from(User user) {

        return FeignUserResDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .slackId(user.getSlackId())
                .userRoles(user.getUserRoles())
                .isDeliver(user.isDeliver()).build();

    }
}
