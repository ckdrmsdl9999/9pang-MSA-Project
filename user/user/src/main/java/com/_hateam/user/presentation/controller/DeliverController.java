package com._hateam.user.presentation.controller;

import com._hateam.common.dto.ResponseDto;
import com._hateam.user.application.dto.DeliverUserCreateReqDto;
import com._hateam.user.application.dto.DeliverUserUpdateReqDto;
import com._hateam.user.application.service.DeliverUserService;
import com._hateam.user.infrastructure.security.UserPrincipals;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-users")
@RequiredArgsConstructor
public class DeliverController {
    private final DeliverUserService deliverUserService;

    //배송 담당자 추가
    @PostMapping("/add")
    public ResponseEntity<?> createDeliverUser(
            @RequestBody DeliverUserCreateReqDto deliverUserCreateReqDto,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(deliverUserService.createDeliverUser(deliverUserCreateReqDto)));
    }

    // 검색(페이징추가, 권한별 분리)
    @GetMapping("/admin/search")
    public ResponseEntity<?> searchDeliverUsers(
            @RequestParam String name,@RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order, @PageableDefault(page = 0, size = 10) Pageable pageable,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(deliverUserService.searchDeliverUsersByName(name,userPrincipals, sortBy, order, pageable)));
    }

    // 배송 담당자 목록 조회(권한별 분리)
    @GetMapping("/")
    public ResponseEntity<?> getAllDeliverUsers(@AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(deliverUserService.getAllDeliverUsers(userPrincipals)));
    }

    // 배송 담당자 단일 조회(권한별 분리)
    @GetMapping("/{deliverId}")
    public ResponseEntity<?> getDeliverUser(
            @PathVariable UUID deliverId,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(deliverUserService.getDeliverUserById(deliverId, userPrincipals)));
    }


    // 배송담당자 수정(관리자만)
    @PostMapping("/{deliverId}")
    public ResponseEntity<?> updateDeliverUser(
            @PathVariable UUID deliverId,
            @RequestBody DeliverUserUpdateReqDto updateDto,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(deliverUserService.updateDeliverUser(deliverId, updateDto, userPrincipals)));
    }

    // 배송담당자 삭제(관리자별)
    @DeleteMapping("/{deliverId}")
    public ResponseEntity<?> deleteDeliverUser(
            @PathVariable UUID deliverId,
            @AuthenticationPrincipal UserPrincipals userPrincipals) {
        deliverUserService.deleteDeliverUser(deliverId, userPrincipals);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success("배송담당자 삭제 완료"));
    }


    @GetMapping("/")//업체 소속 배달담당자 조회 Deliverer중 COM
    public ResponseEntity<?> getCompanyDeliver(@AuthenticationPrincipal UserPrincipals userPrincipals) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.success(deliverUserService.getCompanyDeliver()));
    }


    @GetMapping("/")//허브 소속 배달담당자 조회 Deliverer중 COM
    public ResponseEntity<?> getHubDeliver(@AuthenticationPrincipal UserPrincipals userPrincipals) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.success(deliverUserService.getHubDeliver()));
    }


}
