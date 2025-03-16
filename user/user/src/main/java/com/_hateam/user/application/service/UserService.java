package com._hateam.user.application.service;


import com._hateam.user.application.dto.*;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import com._hateam.user.infrastructure.security.UserPrincipals;

import java.util.List;

public interface UserService {


    public User saveUser(UserSignUpReqDto userSignUpReqDto);

   // public void authenUser(String username, String password);

    public void signOut();

    public List<User> getAllUsers();

    public User getUser(Long userId);

    public User updateUser(UserUpdateReqDto userUpdateReqDto, Long userId, UserPrincipals userPrincipals);

    public void deleteUser(Long userId,UserPrincipals userPrincipals);

    public User searchUser(String username,UserPrincipals userPrincipals);

    AuthResponseDto authenticateUser(UserSignInReqDto signInReqDto);

    public User updateUserRole(Long userId, UserRole role);

    public void approveUser(Long userId);

    // AuthResponseDto refreshToken(TokenRefreshRequestDto refreshRequestDto);



}
