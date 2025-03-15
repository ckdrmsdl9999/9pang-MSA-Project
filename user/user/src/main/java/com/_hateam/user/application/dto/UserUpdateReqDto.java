package com._hateam.user.application.dto;

import com._hateam.user.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class UserUpdateReqDto {

    @NotBlank(message = "닉네임(이름)을 적어주세요")
    private String nickname;

    @NotBlank(message = "슬랙Id를 적어주세요")
    private String slackId;


}
