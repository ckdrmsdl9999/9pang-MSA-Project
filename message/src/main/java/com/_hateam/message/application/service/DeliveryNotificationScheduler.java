package com._hateam.message.application.service;

import com._hateam.message.application.dto.request.SlackMessageDailyDeliveryRequest;
import com._hateam.message.domain.model.CompanyDeliveryRoute;
import com._hateam.message.domain.repository.CompanyDeliveryRouteRepository;
import com._hateam.message.infrastructure.client.DeliveryClient;
import com._hateam.message.infrastructure.client.UserClient;
import com._hateam.message.infrastructure.client.dto.DeliverUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryNotificationScheduler {

    private final CompanyDeliveryRouteRepository companyDeliveryRouteRepository;
    private final SlackMessageService slackMessageService;
    private final UserClient userClient;
    private final DeliveryClient deliveryClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${slack.daily.notification.cron}")
    private String dailyNotificationCron;

    // 매일 아침 6시에 실행
    @Scheduled(cron = "${slack.daily.notification.cron}")
    public void sendDailyDeliveryNotifications() {
        log.info("일일 배송 알림 스케줄러 실행 - cron: {}", dailyNotificationCron);

        try {
            // 배송 담당자별 당일 배송 정보 조회
            // TODO: 모든 허브 또는 특정 허브의 업체 배송 담당자들을 조회하기

            Map<UUID, List<CompanyDeliveryRoute>> hubDeliveryRoutesMap = new HashMap<>();

            // 업체 배송담당자 조회
            // TODO: userClient를 통해 담당자 목록 조회
            List<DeliverUserDto> companyDeliverers = userClient.getCompanyDeliverers().getData();

            for (DeliverUserDto deliverer : companyDeliverers) {
                // 담당자별 당일 배송 경로 조회
                List<CompanyDeliveryRoute> todayRoutes = companyDeliveryRouteRepository
                        .findTodayDeliveryRoutesByDeliverId(deliverer.getDeliverId());

                if (!todayRoutes.isEmpty()) {
                    UUID hubId = todayRoutes.get(0).getStartHubId();

                    if (!hubDeliveryRoutesMap.containsKey(hubId)) {
                        hubDeliveryRoutesMap.put(hubId, new ArrayList<>());
                    }

                    hubDeliveryRoutesMap.get(hubId).addAll(todayRoutes);

                    // 배송 담당자마다 sendDailyDeliveryNotice 호출
                    sendNotificationForDeliverer(deliverer, todayRoutes);
                }
            }

            log.info("일일 배송 알림 스케줄러 실행 완료 - 담당자 수: {}", companyDeliverers.size());
        } catch (Exception e) {
            log.error("일일 배송 알림 스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void sendNotificationForDeliverer(DeliverUserDto deliverer, List<CompanyDeliveryRoute> routes) {
        if (routes.isEmpty()) {
            log.info("배송 담당자 {}({})의 오늘 배송 경로가 없습니다.",
                    deliverer.getName(), deliverer.getDeliverId());
            return;
        }

        try {
            // 위치 정보 수집
            List<SlackMessageDailyDeliveryRequest.DeliveryLocation> locations = routes.stream()
                    .map(route -> SlackMessageDailyDeliveryRequest.DeliveryLocation.builder()
                            .destinationId(route.getDestinationCompanyId())
                            .deliveryId(route.getDeliveryId())
                            .name(route.getDestinationName())
                            .address(route.getDestinationAddress())
                            .build())
                    .collect(Collectors.toList());

            // AI를 통한 최적 배송 순서 계산
            // TODO: AI API를 통해 최적 배송 순서 계산 및 업데이트

            SlackMessageDailyDeliveryRequest request = SlackMessageDailyDeliveryRequest.builder()
                    .hubId(routes.get(0).getStartHubId())
                    .delivererId(deliverer.getDeliverId())
                    .deliveryLocations(locations)
                    .build();

            slackMessageService.sendDailyDeliveryNotice(request);

            log.info("배송 담당자 {}({})에게 일일 배송 알림 발송 완료",
                    deliverer.getName(), deliverer.getDeliverId());
        } catch (Exception e) {
            log.error("배송 담당자 {}({})에게 일일 배송 알림 발송 중 오류 발생: {}",
                    deliverer.getName(), deliverer.getDeliverId(), e.getMessage(), e);
        }
    }
}