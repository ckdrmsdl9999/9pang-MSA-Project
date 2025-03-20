package com._hateam.auth.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {
    private String accessToken;
    private String refreshToken;
}
