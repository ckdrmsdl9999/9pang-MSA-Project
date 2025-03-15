package com._hateam.user.application.service;


import com._hateam.user.application.dto.UserSignUpReqDto;
import com._hateam.user.domain.model.User;

public interface UserService {


    public User saveUser(UserSignUpReqDto userSignUpReqDto);

    public void authenUser(String username, String password);

    public void signOut();

    public void getAllUsers();

    public void getUser(String username);

    public void updateUser(String username);

    public void deleteUser(String username);

    public void searchUser(String username);
}
