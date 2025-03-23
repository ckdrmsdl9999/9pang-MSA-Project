package com._hateam.delivery.service;


import com._hateam.common.constant.KafkaTopics;
import com._hateam.common.event.DeliveryCreatedEvent;
import com._hateam.common.event.DeliveryStatusChangedEvent;
import com._hateam.common.event.KafkaEvent;
import com._hateam.common.event.OrderCreatedEvent;
import com._hateam.delivery.entity.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryMessageConsumer {

    private final DeliveryService deliveryService;
    private final DeliveryKafkaService deliveryKafkaService;

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "delivery_group")
    public void handleDeliveryStatusChanged(OrderCreatedEvent event) {
        log.info("주문생성 이벤트 수신: {}", event);

        try {
            Delivery delivery = deliveryService.registerDeliveryAuto(event);
            deliveryKafkaService.deliveryCreatedByKafka(delivery);

        } catch(Exception e) {
            // todo: 실패에 대한 보상처리 과정
            log.error("배송 생성과정에서 오류 발생", e.getMessage(), e);
        }
    }
}