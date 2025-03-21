package com._hateam.user.application.service;


import com._hateam.user.application.dto.*;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.infrastructure.security.UserPrincipals;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {


     UserResponseDto saveUser(UserSignUpReqDto userSignUpReqDto);

     List<UserResponseDto> getAllUsers(UserPrincipals userPrincipals);

     List<FeignCompanyAdminResDto> getCompany();

     List<FeignHubAdminResDto> getHub();

     UserResponseDto getUser(Long userId);

     UserResponseDto updateUser(UserUpdateReqDto userUpdateReqDto, Long userId, UserPrincipals userPrincipals);

     void deleteUser(Long userId,UserPrincipals userPrincipals);

     Page<UserResponseDto> searchUser(String username, UserPrincipals userPrincipals, String sortBy, String order, Pageable pageable);

     AuthResponseDto authenticateUser(UserSignInReqDto signInReqDto);

     UserResponseDto updateUserRole(Long userId, UserRole role,UserPrincipals userPrincipals);

     FeignUserResDto getUserByFeign(Long userId);


     FeignVerifyResDto verifyUserFeign(String username);

     public UserResponseDto getUserByUsername(String username);

}
