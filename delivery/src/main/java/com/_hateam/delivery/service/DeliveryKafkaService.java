package com._hateam.delivery.service;

import com._hateam.common.constant.KafkaTopics;
import com._hateam.common.event.DeliveryCreatedEvent;
import com._hateam.common.event.DeliveryStatusChangedEvent;
import com._hateam.common.event.KafkaEvent;
import com._hateam.delivery.entity.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryKafkaService {

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    /**
     * kafka 통한 상태 수정 메세지
     */
    public void orderUpdateByKafka(Delivery delivery) {
        DeliveryStatusChangedEvent event = DeliveryStatusChangedEvent.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .newStatus(delivery.getStatus().name())
                .statusChangedAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send(KafkaTopics.DELIVERY_STATUS_CHANGED, KafkaTopics.DELIVERY_STATUS_CHANGED, event);
        System.out.println("------------주문상태 수정 message------------");
    }

    public void deliveryCreatedByKafka(Delivery delivery) {
        DeliveryCreatedEvent deliveryCreatedEvent = DeliveryCreatedEvent.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus().name())
                .build();

        kafkaTemplate.send(KafkaTopics.DELIVERY_CREATED, KafkaTopics.DELIVERY_CREATED, deliveryCreatedEvent);
    }
}
