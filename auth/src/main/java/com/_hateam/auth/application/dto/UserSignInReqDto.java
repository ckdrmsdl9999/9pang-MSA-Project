package com._hateam.auth.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Builder
@Setter
public class UserSignInReqDto {
    private String username;

    private String password;


//    public static User toEntity(UserSignInReqDto userSignInReqDto) {
//
//        return User.builder().username(userSignInReqDto.getUsername()).
//                password(userSignInReqDto.getPassword()).build();
//
//    }
}



