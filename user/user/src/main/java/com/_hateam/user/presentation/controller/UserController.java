package com._hateam.user.presentation.controller;

import com._hateam.common.dto.ResponseDto;
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
    public ResponseDto<?> addUser(@RequestBody @Valid UserSignUpReqDto signUpReqDto, BindingResult bindingResult) {

        return ResponseDto.success(HttpStatus.OK,userService.saveUser(signUpReqDto));
    }

    @PostMapping("/signin")//로그인
    public  ResponseDto<?> signIn(@RequestBody UserSignInReqDto userSignInReqDto) {

        return ResponseDto.success(HttpStatus.OK, userService.authenticateUser(userSignInReqDto));

    }


    @GetMapping("/")//회원조회(자기자신만)
    public ResponseDto<?> getUser(){
        Long userId = 3L;
        return ResponseDto.success(HttpStatus.OK, userService.getUser(userId));
    }

    @GetMapping("/admin/{userId}")//관리자단일조회
    public ResponseDto<?> getAdminUser(@PathVariable Long userId) {
        return ResponseDto.success(HttpStatus.OK, userService.getUser(userId));
    }

//    @GetMapping("/admin/search")//관리자단일검색
//    public ResponseDto<?> getAdminSearch(){
//
//    }

//    @PatchMapping("/roles/{userId}")//권한수정
//    public void updateRole(@PathVariable Long userId, @RequestBody Role role) {
//
//    }

//    @GetMapping("/search")//유저검색(자기자신만)
//    public ResponseDto<?> userSearch(@RequestParam String username){
//
//        return ResponseDto.success(HttpStatus.OK, userService.getUser(useername));
//    }

//    @GetMapping("/approve/{userId}")//회원가입승인
//    public ResponseDto<?> userApprove(){
//
//    }
//
//    @GetMapping("/")//회원목록조회
//    public ResponseDto<?> getAllUser(){
//    }
//
//    @PatchMapping("/")//회원수정
//    public ResponseDto<?> updateUser(){
//
//    }
//
//    @DeleteMapping("/")//회원탈퇴
//    public ResponseDto<?> deleteUser(@PathVariable Long userId) {
//
//    }

}
