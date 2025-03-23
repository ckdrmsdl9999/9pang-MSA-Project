package com._hateam.user.application.dto;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeignCompanyAdminResDto {
    // User 정보
    private Long userId;
    private String username;
    private String slackId;
    private UserRole userRoles;


    public static FeignCompanyAdminResDto from(User user) {
        return FeignCompanyAdminResDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .slackId(user.getSlackId())
                .userRoles(user.getUserRoles())
                .build();
    }

}
