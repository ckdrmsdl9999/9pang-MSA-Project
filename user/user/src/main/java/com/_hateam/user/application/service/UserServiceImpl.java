package com._hateam.user.application.service;

import com._hateam.user.application.dto.*;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.UserRepository;
import com._hateam.user.infrastructure.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public User saveUser(UserSignUpReqDto userSignUpReqDto) {
     return userRepository.save(UserSignUpReqDto.toEntity(userSignUpReqDto,passwordEncoder));
    }

    @Override
    public void authenUser(String username, String password){

    };

    @Override
    public void signOut(){

    };

    @Override
    public void getAllUsers(){
        userRepository.findAllByDeletedAtIsNull();
    };

    @Override
    public User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다 " + userId));
    };

    @Transactional
    @Override
    public void updateUser(UserUpdateReqDto userUpdateReqDto){
        User existingUser = userRepository.findByUsername(userUpdateReqDto.getNickname())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 업데이트할 필드들만 설정
        if (userUpdateReqDto.getNickname() != null) {
            existingUser.setNickname(userUpdateReqDto.getNickname());
        }
        if (userUpdateReqDto.getSlackId() != null) {
            existingUser.setNickname(userUpdateReqDto.getSlackId());
        }
    };

    @Transactional
    @Override
    public void deleteUser(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(username);
        userRepository.save(user);
    };

    @Override
    public void searchUser(String username){

    };

    @Transactional
    public AuthResponseDto authenticateUser(UserSignInReqDto signInReqDto) {
//        // Authenticate the user
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        signInReqDto.getUsername(),
//                        signInReqDto.getPassword()
//                )
//        );

        // If authentication is successful, generate JWT tokens
        User user = userRepository.findByUsername(signInReqDto.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다: " + signInReqDto.getUsername()));

        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUserRoles().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        // Return authentication response with tokens and user info
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .role(user.getUserRoles().name())
                .build();
    }


//    @Transactional
//    @Override
//    public AuthResponseDto refreshToken(TokenRefreshRequestDto refreshRequestDto) {
//        String refreshToken = refreshRequestDto.getRefreshToken();
//
//        // 리프레시 토큰 유효성 검사
//        if (refreshToken == null) {
//            throw new RuntimeException("리프레시 토큰이 없습니다");
//        }
//
//        try {
//            // 리프레시 토큰에서 정보 추출
//            String username = jwtUtil.extractUsername(refreshToken);
//            String tokenType = jwtUtil.extractTokenType(refreshToken);
//
//            // 리프레시 토큰인지 확인
//            if (!"refresh".equals(tokenType)) {
//                throw new RuntimeException("유효하지 않은 토큰 타입입니다");
//            }
//
//            // 사용자 정보 조회
//            User user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다: " + username));
//
//            // 새 액세스 토큰 생성
//            String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getUserId(), user.getUserRoles().name());
//
//            // 새 토큰 응답 반환
//            return AuthResponseDto.builder()
//                    .accessToken(newAccessToken)
//                    .refreshToken(refreshToken) // 동일한 리프레시 토큰 반환
//                    .userId(user.getUserId())
//                    .email(user.getUsername())
//                    .role(user.getUserRoles().name())
//                    .build();
//
//        } catch (Exception e) {
//            throw new RuntimeException("토큰 갱신 오류: " + e.getMessage());
//        }
//    }


}
