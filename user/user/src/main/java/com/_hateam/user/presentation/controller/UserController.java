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
import org.springframework.http.ResponseEntity;
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
        return ResponseDto.success(HttpStatus.OK, userService.getUser(userPrincipals.getId()));
    }

    @GetMapping("/admin/{userId}")//관리자단일조회(MASTER), 스프링시큐리티로 권한 제어 예정
    public ResponseDto<?> getAdminUser(@PathVariable Long userId,@AuthenticationPrincipal UserPrincipals userPrincipals) {
        System.out.println(userPrincipals.getId()+"테스토");
        return ResponseDto.success(HttpStatus.OK, userService.getUser(userId));
    }

    @GetMapping("/admin/search")//관리자단일검색(MASTER)
    public ResponseDto<?> getAdminSearch(@RequestParam String username,@AuthenticationPrincipal UserPrincipals userPrincipals){


        return ResponseDto.success(HttpStatus.OK, userService.searchUser(username,userPrincipals));
    }

    @PatchMapping("/roles/{userId}") // 권한 수정(MASTER)
    public ResponseDto<?> updateRole(@PathVariable Long userId, @RequestBody RoleUpdateReqDto roleUpdateDto,@AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseDto.success(HttpStatus.OK, userService.updateUserRole(userId, roleUpdateDto.getRole(),userPrincipals));
    }


    @GetMapping("/search") // 유저 검색(자기자신만)
    public ResponseDto<?> userSearch(@RequestParam String username, @AuthenticationPrincipal UserPrincipals userPrincipals) {

        return ResponseDto.success(HttpStatus.OK, userService.searchUser(username,userPrincipals));
    }


    @GetMapping("/getusers")//회원목록 조회(DELIVERY,HUB,MASTER)
    public ResponseDto<?> getAllUser(@AuthenticationPrincipal UserPrincipals userPrincipals){
        return ResponseDto.success(HttpStatus.OK, userService.getAllUsers(userPrincipals));
    }

    @PatchMapping("/{userId}")  // 회원 수정(MASTER)
    public ResponseDto<?> updateUser(@PathVariable String userId,@RequestBody @Valid UserUpdateReqDto userUpdateReqDto,
                                     @AuthenticationPrincipal UserPrincipals userPrincipals) {

        //userService.updateUser(userUpdateReqDto,Long.parseLong(userId));
        return ResponseDto.success(HttpStatus.OK, userService.updateUser(userUpdateReqDto,Long.parseLong(userId),userPrincipals));
    }

    @DeleteMapping("/{userId}")  // 회원 탈퇴(MASTER)
    public ResponseDto<?> deleteUser(@PathVariable Long userId,
                                     @AuthenticationPrincipal UserPrincipals userPrincipals) {

        userService.deleteUser(userId,userPrincipals);  // 또는 username 대신 userId를 사용하도록 서비스 메소드 수정 필요
        return ResponseDto.success(HttpStatus.OK, "회원 탈퇴가 성공적으로 처리되었습니다.");
    }
}
