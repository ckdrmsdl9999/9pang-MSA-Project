package com._hateam.user.application.service;

import com._hateam.user.application.dto.UserSignUpReqDto;
import com._hateam.user.application.dto.UserUpdateReqDto;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        userRepository.findAllByDeletedAtIsNull();
    };

    @Override
    public void getUser(String username){
        userRepository.findById(username);
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

        // 소프트 딜리트 구현
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(username);
        userRepository.save(user);
    };

    @Override
    public void searchUser(String username){

    };






}
