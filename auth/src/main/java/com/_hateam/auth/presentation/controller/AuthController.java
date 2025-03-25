package com._hateam.auth.presentation.controller;

import com._hateam.auth.application.dto.*;
import com._hateam.auth.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<ResponseDto<UserSignInResDto>> verifyUser(@RequestBody UserSignInReqDto userSignInReqDto) {
        ResponseDto<UserSignInResDto> response = authService.authenticate(userSignInReqDto);
        return ResponseEntity.ok(response);
    }


}
