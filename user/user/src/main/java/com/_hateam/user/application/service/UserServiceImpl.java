package com._hateam.user.application.service;

import com._hateam.common.dto.ResponseDto;
import com._hateam.common.exception.CustomAccessDeniedException;
import com._hateam.common.exception.CustomForbiddenException;
import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.user.application.dto.*;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.UserRepository;
import com._hateam.user.infrastructure.configuration.ForbiddenException;
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
import java.util.stream.Collectors;

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

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInReqDto.getUsername(),
                            signInReqDto.getPassword()
                    )
            );

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

//    @Override
//    public List<User> getAllUsers(UserPrincipals userPrincipals) {
//        // 권한검증
//        if(userPrincipals.getRole() == UserRole.COMPANY) {
//            throw new CustomForbiddenException("해당 권한으로는 사용할 수 없습니다.");
//        }
//
//        return userRepository.findAllByDeletedAtIsNull();
//    }

    @Override
    public List<UserResponseDto> getAllUsers(UserPrincipals userPrincipals) {
        // 권한검증
        if(userPrincipals.getRole() == UserRole.COMPANY) {
            throw new CustomForbiddenException("해당 권한으로는 사용할 수 없습니다.");
        }
        List<User> users = userRepository.findAllByDeletedAtIsNull();

        return users.stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());

    }

    @Override
    public UserResponseDto getUser(Long userId){

        User user =userRepository.findById(userId)
                .orElseThrow(() -> new CustomForbiddenException("유저를 찾을 수 없습니다 " + userId));
        return UserResponseDto.from(user);
    }

//    @Override
//    public User getUser(Long userId){
//
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new CustomForbiddenException("유저를 찾을 수 없습니다 " + userId));
//    }

    @Transactional
    @Override
    public UserResponseDto updateUser(UserUpdateReqDto userUpdateReqDto, Long userId,UserPrincipals userPrincipals) {
        // 권한검증
        if(userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomNotFoundException("관리자 권한이 필요합니다.");
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException("사용자를 찾을 수 없습니다."));

        // 업데이트할 필드들만 설정
        if (userUpdateReqDto.getNickname() != null) {
            existingUser.setNickname(userUpdateReqDto.getNickname());
        }
        if (userUpdateReqDto.getSlackId() != null) {
            existingUser.setSlackId(userUpdateReqDto.getSlackId());
        }
        if (userUpdateReqDto.getHubId() != null) {
            existingUser.setHubId(userUpdateReqDto.getHubId());
        }
        User updatedUser = userRepository.save(existingUser);
     return UserResponseDto.from(updatedUser);
    }

//    @Transactional
//    @Override
//    public User updateUser(UserUpdateReqDto userUpdateReqDto, Long userId,UserPrincipals userPrincipals) {
//        // 권한검증
//        if(userPrincipals.getRole() != UserRole.ADMIN) {
//            throw new CustomForbiddenException("관리자 권한이 필요합니다.");
//        }
//
//        User existingUser = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomForbiddenException("사용자를 찾을 수 없습니다."));
//
//        // 업데이트할 필드들만 설정
//        if (userUpdateReqDto.getNickname() != null) {
//            existingUser.setNickname(userUpdateReqDto.getNickname());
//        }
//        if (userUpdateReqDto.getSlackId() != null) {
//            existingUser.setSlackId(userUpdateReqDto.getSlackId());
//        }
//        if (userUpdateReqDto.getHubId() != null) {
//            existingUser.setHubId(userUpdateReqDto.getHubId());
//        }
//        return existingUser;
//    }


    @Transactional
    @Override
    public void deleteUser(Long userId, UserPrincipals userPrincipals) {
        // 권한검증
        if(userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomForbiddenException("관리자 권한이 필요합니다.");
        }

        // ADMIN 권한이 있을 때 실행할 코드
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomForbiddenException("사용자를 찾을 수 없습니다."));

            user.setDeletedAt(LocalDateTime.now());
            user.setDeletedBy(user.getUsername());
            userRepository.save(user);


    }

//    @Override
//    public User searchUser(String username,UserPrincipals userPrincipals) {
//        // 본인 검색인지 확인
//        System.out.println(username+"값확인"+userPrincipals.getUsername());
//        if (!userPrincipals.getUsername().equals(username)&&userPrincipals.getRole() != UserRole.ADMIN) {
//            throw new CustomForbiddenException("본인의 아이디만 조회가능합니다.");
//        }else if(userPrincipals.getUsername().equals(username)&&userPrincipals.getRole() != UserRole.ADMIN){
//            return userRepository.findByUsername(username)
//                    .orElseThrow(() -> new CustomForbiddenException("사용자를 찾을 수 없습니다: " + username+"z"));
//        }
//
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new CustomForbiddenException("사용자를 찾을 수 없습니다: " + username+"x"));
//    }

    @Override
    public User searchUser(String username,UserPrincipals userPrincipals) {
        // 본인 검색인지 확인
        System.out.println(username+"값확인"+userPrincipals.getUsername());
        if (!userPrincipals.getUsername().equals(username)&&userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomForbiddenException("본인의 아이디만 조회가능합니다.");
        }else if(userPrincipals.getUsername().equals(username)&&userPrincipals.getRole() != UserRole.ADMIN){
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new CustomForbiddenException("사용자를 찾을 수 없습니다: " + username+"z"));
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomForbiddenException("사용자를 찾을 수 없습니다: " + username+"x"));
    }


    @Transactional
    @Override
    public User updateUserRole(Long userId, UserRole role,UserPrincipals userPrincipals) {
        // 권한검증
        if(userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomForbiddenException("관리자 권한이 필요합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomForbiddenException("사용자를 찾을 수 없습니다: " + userId));

        try {
            user.setUserRoles(role);
            return userRepository.save(user);
        } catch (CustomForbiddenException e) {
            throw new CustomForbiddenException("유효하지 않은 권한입니다: " + role);
        }
    }



}
