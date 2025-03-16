package com._hateam.user.presentation.controller;

import com._hateam.common.dto.ResponseDto;
import com._hateam.user.application.dto.RoleUpdateReqDto;
import com._hateam.user.application.dto.UserSignInReqDto;
import com._hateam.user.application.dto.UserSignUpReqDto;
import com._hateam.user.application.dto.UserUpdateReqDto;
import com._hateam.user.application.service.UserService;
import com._hateam.user.infrastructure.security.UserPrincipals;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public  ResponseDto<?> signIn(@RequestBody @Valid UserSignInReqDto userSignInReqDto,BindingResult bindingResult) {

        return ResponseDto.success(HttpStatus.OK, userService.authenticateUser(userSignInReqDto));

    }

    @GetMapping("/")//회원조회(자기자신만)
    public ResponseDto<?> getUser(@AuthenticationPrincipal UserPrincipals userPrincipals) {
       // System.out.println(userPrincipals.getId()+"값 확인!!"+ userPrincipals.getAuthorities()+"체크!"+ userPrincipals.getPassword());
        return ResponseDto.success(HttpStatus.OK, userService.getUser(userPrincipals.getId()));
    }

    @GetMapping("/admin/{userId}")//관리자단일조회(MASTER)
    public ResponseDto<?> getAdminUser(@PathVariable Long userId,@AuthenticationPrincipal UserPrincipals userPrincipals) {
        //관리자만 볼수있게아직적용x
        return ResponseDto.success(HttpStatus.OK, userService.getUser(userId));
    }

    @GetMapping("/admin/search")//관리자단일검색(MASTER)
    public ResponseDto<?> getAdminSearch(@RequestParam String username,@AuthenticationPrincipal UserPrincipals userPrincipals){

        // 관리자 권한 체크 로직 (필요시 추가)
        return ResponseDto.success(HttpStatus.OK, userService.searchUser(username));
    }

    @PatchMapping("/roles/{userId}") // 권한 수정(MASTER)
    public ResponseDto<?> updateRole(@PathVariable Long userId, @RequestBody RoleUpdateReqDto roleUpdateDto) {
        return ResponseDto.success(HttpStatus.OK, userService.updateUserRole(userId, roleUpdateDto.getRole()));
    }


    @GetMapping("/search") // 유저 검색(자기자신만)
    public ResponseDto<?> userSearch(@RequestParam String username, @AuthenticationPrincipal UserPrincipals userPrincipals) {

        return ResponseDto.success(HttpStatus.OK, userService.searchUser(username));
    }

//    @GetMapping("/approve/{userId}")//회원가입승인
//    public ResponseDto<?> userApprove(){
//
//    }
//
    @GetMapping("/getusers")//회원목록 조회(DELIVERY,HUB,MASTER)
    public ResponseDto<?> getAllUser(){
        return ResponseDto.success(HttpStatus.OK, userService.getAllUsers());
    }

    @PatchMapping("/{userId}")  // 회원 수정(MASTER)
    public ResponseDto<?> updateUser(@PathVariable String userId,@RequestBody @Valid UserUpdateReqDto userUpdateReqDto,
                                     @AuthenticationPrincipal UserPrincipals userPrincipals) {


        //userService.updateUser(userUpdateReqDto,Long.parseLong(userId));
        return ResponseDto.success(HttpStatus.OK, userService.updateUser(userUpdateReqDto,Long.parseLong(userId)));
    }

    @DeleteMapping("/{userId}")  // 회원 탈퇴(MASTER)
    public ResponseDto<?> deleteUser(@PathVariable Long userId,
                                     @AuthenticationPrincipal UserPrincipals userPrincipals) {


        userService.deleteUser(userPrincipals.getId());  // 또는 username 대신 userId를 사용하도록 서비스 메소드 수정 필요
        return ResponseDto.success(HttpStatus.OK, "회원 탈퇴가 성공적으로 처리되었습니다.");
    }
}
