package com._hateam.user.presentation.controller;

import com._hateam.user.application.dto.DeliverUserCreateReqDto;
import com._hateam.user.application.dto.DeliverUserResponseDto;
import com._hateam.user.application.dto.DeliverUserUpdateReqDto;
import com._hateam.user.application.service.DeliverUserService;
import com._hateam.user.application.service.UserService;
import com._hateam.user.domain.model.User;
import com._hateam.user.infrastructure.security.UserPrincipals;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-users")
@RequiredArgsConstructor
public class DeliverController {
    private final DeliverUserService deliverUserService;
    private final UserService userService;

    //배송담당자 추가
    @PostMapping("/add")
    public ResponseEntity<?> createDeliverUser(//hub 아이디는 임의값
            @RequestBody DeliverUserCreateReqDto deliverUserCreateReqDto,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {

        return ResponseEntity.status(HttpStatus.CREATED).body(deliverUserService.createDeliverUser(deliverUserCreateReqDto));

    }

    // 마스터 관리자 검색
    @GetMapping("/admin/search")
    public ResponseEntity<?> searchDeliverUsers(
            @RequestParam String name,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {

        List<DeliverUserResponseDto> results = deliverUserService.searchDeliverUsersByName(name, userPrincipals);
        return ResponseEntity.ok(results);
    }

    // 마스터 관리자 전체 조회
    @GetMapping("/admin/")
    public ResponseEntity<?> getAllDeliverUsers(
            @AuthenticationPrincipal UserPrincipals userPrincipals) {

        List<DeliverUserResponseDto> deliverUsers = deliverUserService.getAllDeliverUsers(userPrincipals);
        return ResponseEntity.ok(deliverUsers);
    }

    // 배송담당자 단일 조회
    @GetMapping("/")
    public ResponseEntity<?> getDeliverUser(
            @PathVariable UUID deliverId,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {

        DeliverUserResponseDto deliverUser = deliverUserService.getDeliverUserById(deliverId, userPrincipals);
        return ResponseEntity.ok(deliverUser);
    }


    // 배송담당자 수정
    @PostMapping("/{deliverId}")
    public ResponseEntity<?> updateDeliverUser(
            @PathVariable UUID deliverId,
            @RequestBody DeliverUserUpdateReqDto updateDto,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {

        DeliverUserResponseDto updatedDeliverUser = deliverUserService.updateDeliverUser(deliverId, updateDto, userPrincipals);
        return ResponseEntity.ok(updatedDeliverUser);
    }


}
