package com._hateam.user.application.service;

import com._hateam.common.exception.CustomForbiddenException;
import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.user.application.dto.*;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.UserRepository;
import com._hateam.user.infrastructure.security.JwtUtil;
import com._hateam.user.infrastructure.security.UserPrincipals;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public UserResponseDto saveUser(UserSignUpReqDto userSignUpReqDto) {
     User savedUser =userRepository.save(UserSignUpReqDto.toEntity(userSignUpReqDto,passwordEncoder));
        return UserResponseDto.from(savedUser);
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
    public UserResponseDto getUser(Long userId){//추후 권한에 따라
        User user =userRepository.findById(userId)
                .orElseThrow(() -> new CustomForbiddenException("유저를 찾을 수 없습니다 " + userId));
        return UserResponseDto.from(user);
    }


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
        existingUser.setUpdatedBy(userPrincipals.getUsername());
        existingUser.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(existingUser);
     return UserResponseDto.from(updatedUser);
    }

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

    @Override
    public Page<UserResponseDto> searchUser(String username, UserPrincipals userPrincipals,
                                            String sortBy, String order, Pageable pageable) {
        // 페이지 사이즈 적용
        if(pageable.getPageSize() != 10 && pageable.getPageSize() != 20 && pageable.getPageSize() != 30) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }

        // 정렬 설정 적용
        Sort sort = Sort.by(order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        // PageRequest에 정렬 적용
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<User> userPage;

        // 권한별 처리
        if (userPrincipals.getRole() == UserRole.ADMIN) {
            // 관리자는 모든 사용자 검색 가능
            userPage = userRepository.findByUsernameContainingAndDeletedAtIsNull(username, pageRequest);
        } else {
            // 일반 사용자(COMPANY, HUB, DELIVERY)는 자신의 정보만 조회 가능
            if (!userPrincipals.getUsername().equals(username)) {
                throw new CustomForbiddenException("본인의 아이디만 조회가능합니다.");
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new CustomNotFoundException("사용자를 찾을 수 없습니다: " + username));

            // 단일 사용자를 Page 객체로 변환
            userPage = new PageImpl<>(List.of(user), pageRequest, 1);
        }

        // User 엔티티를 UserResponseDto로 변환
        return userPage.map(UserResponseDto::from);
    }

    @Transactional
    @Override
    public UserResponseDto updateUserRole(Long userId, UserRole role,UserPrincipals userPrincipals) {
        // 권한검증
        if(userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomForbiddenException("관리자 권한이 필요합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException("사용자를 찾을 수 없습니다: " + userId));


        user.setUserRoles(role);
        User updatedUser = userRepository.save(user);
        return UserResponseDto.from(updatedUser);

    }

    @Override
    public List<FeignCompanyAdminResDto> getCompany() {
        List<User> companyUsers = userRepository.findAllByUserRolesAndDeletedAtIsNull(UserRole.COMPANY);

        return companyUsers.stream()
                .map(FeignCompanyAdminResDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeignHubAdminResDto> getHub() {
        List<User> hubUsers = userRepository.findAllByUserRolesAndDeletedAtIsNull(UserRole.HUB);

        return hubUsers.stream()
                .map(FeignHubAdminResDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public FeignUserResDto getUserByFeign(Long userId){//추후 권한에 따라
        User user =userRepository.findById(userId)
                .orElseThrow(() -> new CustomForbiddenException("유저를 찾을 수 없습니다 " + userId));
        return FeignUserResDto.from(user);
    }

}
