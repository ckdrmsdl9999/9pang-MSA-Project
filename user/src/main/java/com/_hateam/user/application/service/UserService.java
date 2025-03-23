package com._hateam.user.application.service;


import com._hateam.user.application.dto.*;
import com._hateam.user.domain.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {


     UserResponseDto saveUser(UserSignUpReqDto userSignUpReqDto);

     List<UserResponseDto> getAllUsers(String userRole);

     List<FeignCompanyAdminResDto> getCompany();

     List<FeignHubAdminResDto> getHub();

     UserResponseDto getUser(Long userId);

     UserResponseDto updateUser(UserUpdateReqDto userUpdateReqDto,String userMyId, Long userId, String userRole);

     void deleteUser(Long userId, String userRole);

     Page<UserResponseDto> searchUser(String username, String userRole, String userId, String sortBy, String order, Pageable pageable);

     AuthResponseDto authenticateUser(UserSignInReqDto signInReqDto);

     UserResponseDto updateUserRole(Long userId, UserRole role, String userRole);

     FeignUserResDto getUserByFeign(Long userId);


     FeignVerifyResDto verifyUserFeign(String username);

     public UserResponseDto getUserByUsername(String username);

}
