package com._hateam.user.presentation.controller;

import com._hateam.user.application.service.AdminDataService;
import com._hateam.user.application.service.AdminDataServiceImpl;
import com._hateam.user.application.service.DeliverUserService;
import com._hateam.user.domain.enums.DeliverType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-users")
public class DeliverUserController {

    private final DeliverUserService deliverUserService;
    private final AdminDataService adminDataService;

    public DeliverUserController(DeliverUserService deliverUserService, AdminDataService adminDataService) {
        this.deliverUserService = deliverUserService;
        this.adminDataService = adminDataService;
    }

    /**
     * 사용예시
     * [GET] /api/v1/deliver-assign?deliverType=DELIVER_HUB
     * [GET] /api/v1/deliver-assign?deliverType=DELIVER_COMPANY&hubId={some-uuid}
     */
    @GetMapping("/assign")
    public UUID assignDeliverUser(
            @RequestParam("deliverType") DeliverType deliverType,
            @RequestParam(value = "hubId", required = false) UUID hubId
    ) {
        return deliverUserService.assignNextDeliverUser(deliverType, hubId);
    }

    //  마스터 관리자만 호출 가능하다고 가정
    @PostMapping("/init-dummy-data")
    public ResponseEntity<?> initDummyData(){

        // 더미 데이터 삽입
        adminDataService.bulkInsertDummyData();
        return ResponseEntity.ok("더미 배송담당자 180명 생성 완료");
    }
}
