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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<?> signIn(@RequestBody @Valid UserSignInReqDto userSignInReqDto,BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(HttpStatus.OK, userService.authenticateUser(userSignInReqDto)));
    }

    @GetMapping("/getuser")//자기자신 회원조회
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.success(userService.getUser(userPrincipals.getId())));
    }

    @GetMapping("/admin/{userId}")//관리자단일조회(MASTER), 스프링시큐리티로 권한 제어 예정
    public ResponseEntity<?> getAdminUser(@PathVariable Long userId,@AuthenticationPrincipal UserPrincipals userPrincipals) {
        System.out.println(userPrincipals.getId()+"테스토");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(HttpStatus.OK, userService.getUser(userId)));
    }


    @PatchMapping("/roles/{userId}") // 권한 수정(MASTER)
    public ResponseEntity<?> updateRole(@PathVariable Long userId, @RequestBody RoleUpdateReqDto roleUpdateDto,@AuthenticationPrincipal UserPrincipals userPrincipals) {
    return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(userService.updateUserRole(userId, roleUpdateDto.getRole(),userPrincipals)));
    }

    @GetMapping("/search") // 유저 검색(권한에 따라)
    public ResponseEntity<?> userSearch(@RequestParam String username, @RequestParam(defaultValue = "createdAt") String sortBy,
                                        @RequestParam(defaultValue = "desc") String order, @PageableDefault(page = 0, size = 10) Pageable pageable, @AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(HttpStatus.OK, userService.searchUser(username,userPrincipals,sortBy,order,pageable)));
    }

    @GetMapping("/getusers")//회원목록 조회(DELIVERY,HUB,MASTER)
    public ResponseEntity<?> getAllUser(@AuthenticationPrincipal UserPrincipals userPrincipals){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(HttpStatus.OK, userService.getAllUsers(userPrincipals)));
    }

    @PatchMapping("/{userId}")  // 회원 수정(MASTER)
    public ResponseEntity<?> updateUser(@PathVariable String userId,@RequestBody @Valid UserUpdateReqDto userUpdateReqDto,
                                     @AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userUpdateReqDto,Long.parseLong(userId),userPrincipals));
    }

    @DeleteMapping("/{userId}")  // 회원 탈퇴(MASTER)
    public ResponseEntity<?> deleteUser(@PathVariable Long userId,
                                     @AuthenticationPrincipal UserPrincipals userPrincipals) {
        userService.deleteUser(userId,userPrincipals);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(HttpStatus.OK, "회원 탈퇴가 성공적으로 처리되었습니다."));
    }

    @GetMapping("/hub-admin")//허브의 관리자조회(Feign, ROLE=HUB)(
    public ResponseEntity<?> getHubAdmin() {

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.success(userService.getHub()));
    }

    @GetMapping("/company-admin")//업체 관리자 조회(Feign,ROLE=COMPANY)
    public ResponseEntity<?> getCompanyAdmin() {

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.success(userService.getCompany()));
    }

    @GetMapping("/{userId}/message")//사용자정보조회(Feign)
    public ResponseEntity<?> getUserByMessage(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.success(userService.getUserByFeign(userId)));
    }

    @GetMapping("/verify/signin")//이름으로아이디,패스워드조회(Feign)
    public ResponseEntity<?> findByUsername(@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.success(userService.verifyUserFeign(username)));
    }


}
