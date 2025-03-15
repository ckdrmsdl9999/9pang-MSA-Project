package com._hateam.user.application.service;

import com._hateam.user.application.dto.UserSignUpReqDto;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User saveUser(UserSignUpReqDto userSignUpReqDto) {
     return userRepository.save(UserSignUpReqDto.toEntity(userSignUpReqDto));
    }

    @Override
    public void authenUser(String username, String password){

    };

    @Override
    public void signOut(){

    };

    @Override
    public void getAllUsers(){

    };

    @Override
    public void getUser(String username){

    };
    @Override
    public void updateUser(String username){

    };


    @Override
    public void deleteUser(String username){

    };

    @Override
    public void searchUser(String username){

    };






}
