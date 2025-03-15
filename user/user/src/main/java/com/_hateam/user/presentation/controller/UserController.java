package com._hateam.user.presentation.controller;

import com._hateam.user.application.dto.UserSignInReqDto;
import com._hateam.user.application.dto.UserSignUpReqDto;
import com._hateam.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class UserController {

    @Autowired
    private final UserService userService;

    @PostMapping("/signup")//회원가입
    public ResponseEntity<?> addUser(@RequestBody @Valid UserSignUpReqDto signUpReqDto, BindingResult bindingResult) {

        System.out.println(signUpReqDto.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(userService.saveUser(signUpReqDto));
    }

    @PostMapping("/signin")//로그인
    public void signIn(@RequestBody UserSignInReqDto user) {



    }

    @GetMapping("/{userId}")//회원조회(자기자신만)
    public void getUser(){

    }

    @GetMapping("/admin/{userId}")//관리자단일조회
    public void getAdminUser(){

    }

    @GetMapping("/admin/search")//관리자단일검색
    public void getAdminSearch(){

    }

//    @PatchMapping("/roles/{userId}")//권한수정
//    public void updateRole(@PathVariable Long userId, @RequestBody Role role) {
//
//    }

    @GetMapping("/search")//유저검색(자기자신만)
    public void userSearch(){

    }

    @GetMapping("/approve/{userId}")//회원가입승인
    public void userApprove(){

    }

    @GetMapping("/")//회원목록조회
    public void getAllUser(){
    }

    @PatchMapping("/")//회원수정
    public void updateUser(){

    }

    @DeleteMapping("/")//회원탈퇴
    public void deleteUser(@PathVariable Long userId) {

    }

}
