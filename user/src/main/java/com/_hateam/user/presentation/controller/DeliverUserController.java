package com._hateam.user.presentation.controller;

import com._hateam.user.application.service.AdminDataService;
import com._hateam.user.application.service.DeliverUserService;
import com._hateam.user.domain.enums.DeliverType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliver-assign")
public class DeliverUserController {

    private final DeliverUserService deliverUserService;
    private final AdminDataService adminDataService;
    public DeliverUserController(DeliverUserService deliverUserService, AdminDataService adminDataService) {
        this.deliverUserService = deliverUserService;
        this.adminDataService = adminDataService;
    }

    /**
     * 예시:
     * [GET] /api/v1/deliver-assign?deliverType=HUB
     * [GET] /api/v1/deliver-assign?deliverType=COMPANY&hubId={some-uuid}
     */
    @GetMapping
    public UUID assignDeliverUser(
            @RequestParam("deliverType") DeliverType deliverType,
            @RequestParam(value = "hubId", required = false) UUID hubId
    ) {
        return deliverUserService.assignNextDeliverUser(deliverType, hubId);
    }

    // 예) 마스터 관리자만 호출 가능하다고 가정
    @PostMapping("/init-dummy-data")
    public ResponseEntity<?> initDummyData(
//            @RequestHeader("x-user-role") String userRole)
    ){
//        if (!"ADMIN".equals(userRole)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("관리자 권한이 필요합니다.");
//        }

        // 더미 데이터 삽입
        adminDataService.bulkInsertDummyData();

        return ResponseEntity.ok("더미 배송담당자 180명 생성 완료");
    }
}
