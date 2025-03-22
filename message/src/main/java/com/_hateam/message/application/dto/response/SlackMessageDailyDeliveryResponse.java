package com._hateam.message.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackMessageDailyDeliveryResponse {
    private UUID hubId;                           // 출발 허브 ID
    private UUID delivererId;                     // 배송 담당자 ID
    private String delivererName;                 // 배송 담당자 이름
    private String slackId;                       // 배송 담당자 슬랙 ID
    private int totalDeliveryPoints;              // 총 배송 지점 수
    private String sentAt;                        // 알림 발송 시간
    private List<DeliveryRoutePoint> routePoints; // 방문 지점 정보 리스트

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryRoutePoint {
        private int sequence;                     // 방문 순서
        private UUID destinationId;               // 목적지 ID (업체 ID 등)
        private String destinationName;           // 목적지 이름
        private String destinationAddress;        // 목적지 주소
    }
}