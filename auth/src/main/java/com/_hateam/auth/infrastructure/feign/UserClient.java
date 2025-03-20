package com._hateam.auth.infrastructure.feign;


import com._hateam.auth.application.dto.FeignUserDto;
import com._hateam.auth.application.dto.FeignVerifyResDto;
import com._hateam.auth.application.dto.ResponseDto;
import com._hateam.auth.application.dto.UserSignInReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name="user",url="${services.user.url}")
public interface UserClient {

    @GetMapping("/api/users/verify/signin")
    ResponseEntity<ResponseDto<FeignVerifyResDto>> findByUsername(@RequestParam String username);
//    ResponseEntity<ResponseDto<FeignVerifyResDto>> findByUsername(UserSignInReqDto userSignInReqDto);
    //dto자체생성


}
