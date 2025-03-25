package com._hateam.user.application.dto;

import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@Setter
public class UserSignUpReqDto {

    @NotBlank(message = "아이디값이 필요합니다")
    @Pattern(regexp = "^[a-z0-9]{4,10}$",
            message = "최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)여야 합니다.")
    private String username;//최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)

    @NotBlank(message = "패스워드가 필요합니다")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자가 포함되어야 합니다.")
    private String password;//최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자

    @NotBlank(message = "닉네임(이름)을 적어주세요")
    private String nickname;

    @NotBlank(message = "슬랙Id를 적어주세요")
    private String slackId;

    @NotNull(message = "역할을 선택해주세요")
    private UserRole role;

    @NotNull(message = "배송담당자 여부를 선택해주세요")
    private boolean isDeliver;


    public static User toEntity(UserSignUpReqDto userSignUpReqDto, PasswordEncoder passwordEncoder) {

        return User.builder().username(userSignUpReqDto.getUsername()).password(passwordEncoder.encode(userSignUpReqDto.getPassword())).
                userRoles(userSignUpReqDto.getRole()).slackId(userSignUpReqDto.getSlackId()).
                nickname(userSignUpReqDto.getNickname()).isDeliver(false).build();

    }


}
