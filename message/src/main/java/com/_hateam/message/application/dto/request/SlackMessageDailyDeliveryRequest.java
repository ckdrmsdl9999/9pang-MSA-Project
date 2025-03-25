package com._hateam.message.application.dto.request;

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
public class SlackMessageDailyDeliveryRequest {
    private UUID hubId;                // 허브 ID
    private UUID delivererId;          // 배송 담당자 ID
    private List<DeliveryLocation> deliveryLocations;  // 배송 위치 목록

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryLocation {
        private UUID destinationId;    // 목적지 ID (업체 ID 등)
        private String name;           // 장소명
        private String address;        // 주소
        private UUID deliveryId;       // 배송 ID (해당되는 경우)
    }
}