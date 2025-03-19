package com._hateam.user.application.dto;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    // User 정보
    private Long userId;
    private String username;
    private String nickname;
    private String slackId;
    private UUID hubId;
    private UserRole userRoles;
    private boolean isDeliver;
    // 배송담당자 ID만 포함(유저를 통해 deliverId가져오기 위해)
    private UUID deliverId;


    public static UserResponseDto from(User user) {
        UserResponseDtoBuilder builder = UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .slackId(user.getSlackId())
                .hubId(user.getHubId())
                .userRoles(user.getUserRoles())
                .deliverId(user.isDeliver() && user.getDeliverUser() != null ?
                user.getDeliverUser().getDeliverId() : null)
                .isDeliver(user.isDeliver());

        return builder.build();
    }

}
