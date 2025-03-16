package com._hateam.user.application.service;


import com._hateam.user.application.dto.*;
import com._hateam.user.domain.model.User;

public interface UserService {


    public User saveUser(UserSignUpReqDto userSignUpReqDto);

    public void authenUser(String username, String password);

    public void signOut();

    public void getAllUsers();

    public User getUser(Long userId);

    public void updateUser(UserUpdateReqDto userUpdateReqDto);

    public void deleteUser(String username);

    public void searchUser(String username);

    AuthResponseDto authenticateUser(UserSignInReqDto signInReqDto);

    // AuthResponseDto refreshToken(TokenRefreshRequestDto refreshRequestDto);



}
