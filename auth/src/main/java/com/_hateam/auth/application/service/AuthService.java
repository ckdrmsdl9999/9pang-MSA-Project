package com._hateam.auth.application.service;

import com._hateam.auth.application.dto.*;
import com._hateam.auth.domain.TokenInfo;
import com._hateam.auth.infrastructure.config.JwtUtil;
import com._hateam.auth.infrastructure.feign.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
//
//    @Value("${spring.jwt.secret}")
//    private String secret;
//
//    @Value("${spring.jwt.accessTokenValidityInMilliseconds}")
//    private long accessTokenValidity; // 액세스 토큰 유효 시간 (밀리초)
//
//    @Value("${spring.jwt.refreshTokenValidityInMilliseconds}")
//    private long refreshTokenValidity; // 리


    public ResponseDto<UserSignInResDto> authenticate(UserSignInReqDto userSignInReqDto) {
        // 1. Feign 클라이언트로 사용자 정보 조회
        ResponseEntity<ResponseDto<FeignVerifyResDto>> response = userClient.findByUsername(userSignInReqDto.getUsername());

        // 2. 응답 검증
        if (response.getBody() == null || response.getBody().getData() == null) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }


        FeignVerifyResDto userData = response.getBody().getData();

        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(userSignInReqDto.getPassword(), userData.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 4. JWT 토큰 생성 (userId와 role만 포함)
        TokenInfo tokenInfo = jwtUtil.generateToken(
                userData.getUserId(),
                userData.getUserRole().name()
        );

        // 5. 응답 데이터 생성 (정적 팩토리 메서드 사용)
        UserSignInResDto userSignInResDto = UserSignInResDto.from(
                userData.getUserId(),
                userData.getUsername(),
                userData.getUserRole().name(),
                tokenInfo
        );


        return ResponseDto.success(userSignInResDto);
    }

//    // 토큰 재발급 메서드
//    public ResponseDto<UserSignInResDto> refreshToken(String refreshToken) {
//        // 리프레시 토큰 검증
//        if (!jwtUtil.validateToken(refreshToken)) {
//            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
//        }
//
//        // 토큰에서 사용자 정보 추출 (userId와 role만)
//        Long userId = jwtUtil.extractUserId(refreshToken);
//        String role = jwtUtil.extractUserRole(refreshToken);
//
//        // 새 토큰 발급
//        TokenInfo newTokenInfo = jwtUtil.generateToken(userId, role);
//
//        // 응답 데이터 생성 (username이 없으므로 Feign으로 사용자 정보 다시 조회)
//        ResponseEntity<ResponseDto<FeignVerifyResDto>> userResponse = userClient.findById(userId);
//
//        if (userResponse.getBody() == null || userResponse.getBody().getData() == null) {
//            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
//        }
//
//        String username = userResponse.getBody().getData().getUsername();
//        UserSignInResDto userSignInResDto = UserSignInResDto.from(userId, username, role, newTokenInfo);
//
//        return ResponseDto.<UserSignInResDto>builder()
//                .status(200)
//                .message("토큰 재발급 성공")
//                .data(userSignInResDto)
//                .build();
//    }


    }

