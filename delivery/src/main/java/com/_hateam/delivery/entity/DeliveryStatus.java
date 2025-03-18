package com._hateam.delivery.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum DeliveryStatus {
    WAITING_AT_HUB,
    MOVING_TO_HUB,
    ARRIVED_AT_DEST_HUB,
    OUT_FOR_DELIVERY,
    MOVING_TO_COMPANY,
    DELIVERY_COMPLETED;

    public static DeliveryStatus of(String status) {
        return valueOf(status);
    }
}
