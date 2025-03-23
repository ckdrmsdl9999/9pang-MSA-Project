package com._hateam.user.application.service;


import com._hateam.user.application.dto.DeliverUserCreateReqDto;
import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminDataService {

    private final UserRepository userRepository;
    private final DeliverUserService deliverUserService; // 위 createDeliverUser()를 사용하는 Service

    @Transactional
    public void bulkInsertDummyData() {
        // 1) 허브 배송담당자 10명
        for (int i = 1; i <= 10; i++) {
            // 1-A) User 생성
            User user = User.builder()
                    .username("hub_deliver_" + i)  // 예: "hub_deliver_1"
                    .nickname("허브배달원" + i)
                    .password("TEST_PASSWORD") // 실제론 암호화 필요
                    .slackId("SLACK_HUB_" + i)
                    .userRoles(UserRole.DELIVERY)  // 혹은 HUB 등등, 정책에 맞게
                    .isDeliver(true)
                    .build();
            userRepository.save(user);

            // 1-B) DeliverUserCreateReqDto 준비
            DeliverUserCreateReqDto dto = DeliverUserCreateReqDto.builder()
                    .userId(user.getUserId())   // 위에서 save된 user의 PK(Long)
                    .hubId(null)               // 허브배송 → hubId null
                    .slackId(user.getSlackId())
                    .name(user.getNickname())
                    .deliverType(DeliverType.DELIVER_HUB)
                    .contactNumber("010-1111-11" + String.format("%02d", i))
                    .status(Status.ACTIVE)
                    .build();

            // 1-C) createDeliverUser(...) 호출 → rotationOrder는 자동 +1
            deliverUserService.createDeliverUser(dto);
        }

        // 2) 업체 배송담당자 (예: 허브가 17개 있다고 가정)
        //    각 허브마다 10명씩
        for (int hubIndex = 1; hubIndex <= 17; hubIndex++) {
            UUID hubUuid = UUID.randomUUID(); // 실제로는 특정 허브 UUID가 있을 수도 있음

            for (int j = 1; j <= 10; j++) {
                // 2-A) User 생성
                User user = User.builder()
                        .username("company_" + hubIndex + "_deliver_" + j) // 유니크 이름
                        .nickname("업체배달원" + hubIndex + "-" + j)
                        .password("TEST_PASSWORD")
                        .slackId("SLACK_COMPANY_" + hubIndex + "_" + j)
                        .userRoles(UserRole.DELIVERY)
                        .isDeliver(true)
                        .build();
                userRepository.save(user);

                // 2-B) DeliverUserCreateReqDto
                DeliverUserCreateReqDto dto = DeliverUserCreateReqDto.builder()
                        .userId(user.getUserId())
                        .hubId(hubUuid)  // 업체배송 → hubId 있음
                        .slackId(user.getSlackId())
                        .name(user.getNickname())
                        .deliverType(DeliverType.DELIVER_COMPANY)
                        .contactNumber("010-2222-" + String.format("%04d", (hubIndex * 100 + j)))
                        .status(Status.ACTIVE)
                        .build();

                // 2-C) 호출
                deliverUserService.createDeliverUser(dto);
            }
        }
    }

    // 필요하다면, 전체 삭제 후 다시 삽입하는 메서드도 가능
    @Transactional
    public void resetAndInsertDummyData() {
        // 만약 데이터 전부 지우고 싶다면...
        // (주의: 실제 운영 DB에서 deleteAll()은 조심히 사용)
        // userRepository.deleteAll();
        // deliverUserRepository.deleteAll();
        // 등등...

        bulkInsertDummyData();
    }

}
