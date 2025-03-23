// message/src/main/java/com/_hateam/message/infrastructure/messaging/OrderMessageConsumer.java
package com._hateam.message.infrastructure.messaging;

import com._hateam.common.constant.KafkaTopics;
import com._hateam.common.event.OrderCreatedForSlackEvent;
import com._hateam.message.application.dto.request.SlackMessageRequest;
import com._hateam.message.application.service.SlackMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final SlackMessageService slackMessageService;

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED_FOR_SLACK)
    public void handleOrderCreatedForSlack(OrderCreatedForSlackEvent event) {
        log.info("주문 생성 슬랙 알림 이벤트 수신: {}", event);

        try {
            // TODO: AI API를 사용한 최종 발송 시한 계산 로직 구현
            // 현재는 배송 마감일보다 2일 전으로 설정
            LocalDateTime estimatedSendDeadline = event.getDeliveryDeadline().minusDays(2).withHour(9).withMinute(0);
            String formattedDeadline = estimatedSendDeadline.format(DateTimeFormatter.ofPattern("M월 d일 a h시"));

            String message = generateOrderNotificationMessage(event, formattedDeadline);

            SlackMessageRequest request = SlackMessageRequest.builder()
                    .receiverId(event.getDelivererSlackId())
                    .content(message)
                    .build();

            slackMessageService.sendMessage(request);
            log.info("주문 생성 슬랙 알림 발송 완료. 주문 ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("주문 생성 슬랙 알림 발송 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private String generateOrderNotificationMessage(OrderCreatedForSlackEvent event, String estimatedDeadline) {
        String viaHubs = event.getViaHubs().stream()
                .collect(Collectors.joining(", "));

        // 메시지 템플릿 생성
        return String.format(
                "주문 번호 : %s\n" +
                        "주문자 정보 : %s / %s\n" +
                        "상품 정보 : %s\n" +
                        "요청 사항 : %s\n" +
                        "발송지 : %s\n" +
                        "경유지 : %s\n" +
                        "도착지 : %s\n" +
                        "배송담당자 : %s / %s\n\n" +
                        "위 내용을 기반으로 도출된 최종 발송 시한은 %s 입니다.",
                event.getOrderNumber(),
                event.getCustomerName(),
                event.getCustomerEmail(),
                event.getProductInfo(),
                event.getRequestInfo(),
                event.getStartHub(),
                viaHubs,
                event.getDestination(),
                event.getDelivererName(),
                event.getDelivererSlackId(),
                estimatedDeadline
        );
    }
}