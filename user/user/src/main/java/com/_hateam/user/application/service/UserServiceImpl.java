package com._hateam.user.application.service;

import com._hateam.common.dto.ResponseDto;
import com._hateam.common.exception.CustomAccessDeniedException;
import com._hateam.user.application.dto.*;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.UserRepository;
import com._hateam.user.infrastructure.security.JwtUtil;
import com._hateam.user.infrastructure.security.UserPrincipals;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional
    public AuthResponseDto authenticateUser(UserSignInReqDto signInReqDto) {//로그인

//        try {//사용자인증
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInReqDto.getUsername(),
                            signInReqDto.getPassword()
                    )
            );
//        }catch (UsernameNotFoundException e) {
//            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
//        }//글로벌 exception 적용

        // 토큰에 값 담기위해 값 조회
        User user = userRepository.findByUsername(signInReqDto.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다: " + signInReqDto.getUsername()));

        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUserRoles().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        // 응답값 반환
        return AuthResponseDto.builder()
                .accessToken("Bearer " + accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .role(user.getUserRoles().name())
                .build();
    }

    @Override
    public void signOut(){

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByDeletedAtIsNull();
    }


    @Override
    public User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다 " + userId));
    }

    @Transactional
    @Override
    public User updateUser(UserUpdateReqDto userUpdateReqDto,Long userId,UserPrincipals userPrincipals) {
        // 권한검증
        if(userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomAccessDeniedException("관리자 권한이 필요합니다.");
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 업데이트할 필드들만 설정
        if (userUpdateReqDto.getNickname() != null) {
            existingUser.setNickname(userUpdateReqDto.getNickname());
        }
        if (userUpdateReqDto.getSlackId() != null) {
            existingUser.setSlackId(userUpdateReqDto.getSlackId());
        }
     return existingUser;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId, UserPrincipals userPrincipals) {
        // 권한검증
        if(userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomAccessDeniedException("관리자 권한이 필요합니다.");
        }

        // ADMIN 권한이 있을 때 실행할 코드
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

            user.setDeletedAt(LocalDateTime.now());
            user.setDeletedBy(user.getUsername());
            userRepository.save(user);



    }

    @Override
    public User searchUser(String username,UserPrincipals userPrincipals) {

//        // 본인 검색인지 확인
//        if (!userPrincipals.getUsername().equals(username)) {
//            return ResponseDto.failure(HttpStatus.FORBIDDEN, "본인 정보만 검색할 수 있습니다.");
//        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }


    @Transactional
    @Override
    public User updateUserRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        try {
            // String을 Enum으로 변환
          //  UserRole userRole = UserRole.valueOf(role.toUpperCase());
            user.setUserRoles(role);
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 권한입니다: " + role);
        }
    }

    @Transactional
    @Override
    public void approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
//
//        // 승인 상태로 변경
//        user.setApproved(true);
//        userRepository.save(user);
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
