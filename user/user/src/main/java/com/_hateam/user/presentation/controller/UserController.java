package com._hateam.user.presentation.controller;

import com._hateam.common.annotation.UserHeader;
import com._hateam.common.dto.ResponseDto;
import com._hateam.common.dto.UserHeaderInfo;
import com._hateam.user.application.dto.RoleUpdateReqDto;
import com._hateam.user.application.dto.UserSignInReqDto;
import com._hateam.user.application.dto.UserSignUpReqDto;
import com._hateam.user.application.dto.UserUpdateReqDto;
import com._hateam.user.application.service.UserService;
import com._hateam.user.infrastructure.security.UserPrincipals;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> signIn(@RequestBody @Valid UserSignInReqDto userSignInReqDto, BindingResult bindingResult) {
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


    @PutMapping("/roles/{userId}") // 권한 수정(MASTER)
    public ResponseEntity<?> updateRole(@PathVariable Long userId, @RequestBody RoleUpdateReqDto roleUpdateDto,HttpServletRequest request) {
        String userRole = request.getHeader("x-user-role");
        System.out.println(userRole+"권한학인11");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(userService.updateUserRole(userId, roleUpdateDto.getRole(), userRole)));
    }

    @GetMapping("/search") // 유저 검색(권한에 따라)
    public ResponseEntity<?> userSearch(@RequestParam String username, @RequestParam(defaultValue = "createdAt") String sortBy,
                                        @RequestParam(defaultValue = "desc") String order, @PageableDefault(page = 0, size = 10) Pageable pageable,
                                        HttpServletRequest request) {
        String userId = request.getHeader("x-user-id");
        String userRole = request.getHeader("x-user-role");

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(HttpStatus.OK, userService.searchUser(username,userRole,
                userId,sortBy,order,pageable)));
    }

    @GetMapping("/getusers")//회원목록 조회(DELIVERY,HUB,MASTER)
    public ResponseEntity<?> getAllUser(HttpServletRequest request){
        String userRole = request.getHeader("x-user-role");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(HttpStatus.OK, userService.getAllUsers(userRole)));
    }

    @PutMapping("/{userId}")  // 회원 수정(MASTER)
    public ResponseEntity<?> updateUser(@PathVariable String userId,@RequestBody @Valid UserUpdateReqDto userUpdateReqDto,HttpServletRequest request) {
        String userRole = request.getHeader("x-user-role");
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userUpdateReqDto,Long.parseLong(userId),userRole));
    }

    @DeleteMapping("/{userId}")  // 회원 탈퇴(MASTER)
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        String userRole = request.getHeader("x-user-role");
        userService.deleteUser(userId,userRole);
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
    // userId는 외부로 나가면 안되는값, username을 대신 userid 처럼 사용
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.success(userService.getUserByUsername(username)));

    }


    @GetMapping("/headers") //헤더값 확인용
    public ResponseEntity<?> getDirectHeaders(HttpServletRequest request) {

        String userId = request.getHeader("x-user-id");
        String userRole = request.getHeader("x-user-role");

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("userRole", userRole);

        return ResponseEntity.ok(ResponseDto.success(result));
    }
}
