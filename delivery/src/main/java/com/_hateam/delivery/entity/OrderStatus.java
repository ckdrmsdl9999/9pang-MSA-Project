package com._hateam.delivery.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum OrderStatus {
    WAITING,
    IN_DELIVERY,
    DONE;

    public static OrderStatus of(String status) {
        return valueOf(status);
    }
}
