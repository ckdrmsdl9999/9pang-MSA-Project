package com._hateam.message.application.service;

import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.message.application.dto.request.SlackMessageDailyDeliveryRequest;
import com._hateam.message.application.dto.request.SlackMessageRequest;
import com._hateam.message.application.dto.request.SlackMessageSearchRequest;
import com._hateam.message.application.dto.response.SlackMessageDailyDeliveryResponse;
import com._hateam.message.application.dto.response.SlackMessageResponse;
import com._hateam.message.domain.model.SlackMessage;
import com._hateam.message.domain.repository.SlackMessageRepository;
import com._hateam.message.domain.service.SlackMessageDomainService;
import com._hateam.message.infrastructure.client.SlackApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMessageService {

    private final SlackMessageRepository slackMessageRepository;
    private final SlackApiClient slackApiClient;
    private final SlackMessageDomainService domainService;

    /**
     * 일반 슬랙 메시지 발송
     *
     * @param request 메시지 요청 DTO
     * @return 저장된 메시지 응답 DTO
     */
    @Transactional
    public SlackMessageResponse sendMessage(SlackMessageRequest request) {
        if (!domainService.isValidMessage(request.getContent())
                || !domainService.isValidSlackId(request.getReceiverId())) {
            throw new IllegalArgumentException("메시지 또는 수신자 ID가 유효하지 않습니다.");
        }

        SlackMessage message = SlackMessage.builder()
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .status(SlackMessage.MessageStatus.PENDING)
                .build();

        SlackMessage savedMessage = slackMessageRepository.save(message);

        boolean isSuccess = slackApiClient.sendMessage(request.getReceiverId(), request.getContent());

        savedMessage = domainService.updateMessageStatus(savedMessage, isSuccess);
        slackMessageRepository.save(savedMessage);

        return SlackMessageResponse.from(savedMessage);
    }

    /**
     * 배송 경로 메시지 발송
     *
     * @param request 배송 경로 메시지 요청 DTO
     * @return 저장된 메시지 응답 DTO
     */
    @Transactional
    public SlackMessageResponse sendDeliveryRouteMessage(SlackMessageRequest request) {
        // TODO: 배송 경로 관련 처리 기능 추가하기
        log.info("배송 경로 메시지 발송. 수신자: {}", request.getReceiverId());
        return sendMessage(request);
    }

    /**
     * 메시지 조회
     *
     * @param pageable 페이징 정보
     * @return 메시지 목록
     */
    @Transactional(readOnly = true)
    public Page<SlackMessageResponse> getMessages(Pageable pageable) {
        Page<SlackMessage> messages = slackMessageRepository.findAllNotDeleted(pageable);
        return messages.map(SlackMessageResponse::from);
    }

    /**
     * 메시지 검색
     *
     * @param searchRequest 검색 요청 DTO
     * @param pageable      페이징 정보
     * @return 검색 결과
     */
    @Transactional(readOnly = true)
    public Page<SlackMessageResponse> searchMessages(
            SlackMessageSearchRequest searchRequest, Pageable pageable) {

        try {
            Page<SlackMessage> messages = slackMessageRepository.searchMessages(
                    searchRequest.getReceiverId(),
                    searchRequest.getStartDate(),
                    searchRequest.getEndDate(),
                    searchRequest.getKeyword(),
                    pageable
            );

            return messages.map(SlackMessageResponse::from);
        } catch (Exception e) {
            throw new RuntimeException("메시지 검색 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 페이지 크기 검증
     * 
     * @param size 10, 30, 50만 허용
     * @return 기본값 10
     */
    public int validatePageSize(int size) {
        return (size == 10 || size == 30 || size == 50) ? size : 10;
    }

    /**
     * 메시지 상세 조회
     *
     * @param messageId 메시지 ID
     * @return 메시지 상세 정보
     */
    @Transactional(readOnly = true)
    public SlackMessageResponse getMessage(UUID messageId) {
        SlackMessage message = slackMessageRepository.findByIdNotDeleted(messageId)
                .orElseThrow(() -> new CustomNotFoundException("메시지를 찾을 수 없습니다. ID: " + messageId));
        return SlackMessageResponse.from(message);
    }

    /**
     * 일일 배송 알림 발송
     *
     * @param request 일일 배송 알림 요청 DTO
     * @return 일일 배송 알림 결과
     */
    @Transactional
    public SlackMessageDailyDeliveryResponse sendDailyDeliveryNotice(
            SlackMessageDailyDeliveryRequest request) {
        log.info("일일 배송 알림 발송 시작. Hub ID: {}, 발송 시간: {}", request.getHubId(), request.getSendTime());

        LocalDateTime startTime = LocalDateTime.now();
        String batchId = "BATCH-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        // TODO: 해당 허브에 속한 배송담당자 목록 조회 로직 구현하기
        // TODO: 배송담당자 레포지토리를 통해 해당 허브 소속 담당자 조회 구현하기

        // 임시로 5명이 성공, 1명이 실패했다고 가정
        int staffCount = 6;
        int sentMessages = 5;
        int failedMessages = 1;

        // 샘플 메시지
        String sampleMessage = generateDailyRoutePlanMessage();

        // TODO: 각 담당자의 슬랙 ID로 메시지 발송하기
//        for (DeliveryStaff staff : deliveryStaffs) {
//            boolean sent = slackApiClient.sendMessage(staff.getSlackId(), sampleMessage);
//            if (sent) sentMessages++;
//            else failedMessages++;
//        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration executionTime = Duration.between(startTime, endTime);

        return SlackMessageDailyDeliveryResponse.builder()
                .batchId(batchId)
                .staffCount(staffCount)
                .sentMessages(sentMessages)
                .failedMessages(failedMessages)
                .executionTime(executionTime.toSeconds() + "s")
                .build();
    }

    /**
     * 일일 배송 계획 메시지 샘플 생성 (예시용)
     */
    private String generateDailyRoutePlanMessage() {
        // TODO: 실제 배송 계획 데이터에 기반한 메시지 생성하기
        return "안녕하세요, 오늘의 배송 계획입니다.\n\n" +
                "배송 순서:\n" +
                "1. 서울시 강남구 테헤란로 152 (10:00)\n" +
                "2. 서울시 서초구 서초대로 38길 12 (11:30)\n" +
                "3. 서울시 송파구 올림픽로 300 (14:00)\n\n" +
                "총 예상 이동 거리: 23.5km\n" +
                "총 예상 소요 시간: 2시간 15분\n\n" +
                "안전 운행하세요!";
    }

    /**
     * 정해진 시간에 일일 배송 알림 자동 발송 (매일 오전 6시)
     */
    @Scheduled(cron = "${slack.daily.notification.cron}")
    @Transactional
    public void scheduledDailyDeliveryNotice() {
        log.info("일일 배송 알림 스케줄러 실행");

        // TODO: 모든 허브에 대한 배송 알림 발송 로직 구현하기
        // TODO: 허브 리포지토리에서 모든 허브 조회 후 각각에 알림 발송하기
//        List<Hub> hubs = hubRepository.findAll();
//        for (Hub hub : hubs) {
//            SlackMessageDailyDeliveryRequest request = new SlackMessageDailyDeliveryRequest();
//            request.setHubId(hub.getId());
//            request.setSendTime("06:00:00");
//            sendDailyDeliveryNotice(request);
//        }
    }
}