package com._hateam.common.event;

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
public class DailyDeliveryNotificationEvent {
    private UUID hubId;
    private UUID delivererId;
    private List<DeliveryLocation> deliveryLocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryLocation {
        private UUID destinationId;
        private UUID deliveryId;
        private String name;
        private String address;
        private String latitude;  // 위도
        private String longitude; // 경도
    }
}