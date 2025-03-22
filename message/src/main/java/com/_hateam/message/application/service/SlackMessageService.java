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
import com._hateam.message.infrastructure.client.*;
import com._hateam.message.infrastructure.client.dto.DeliverUserDto;
import com._hateam.message.infrastructure.client.dto.DeliveryInfoDto;
import com._hateam.message.infrastructure.client.dto.HubDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMessageService {

    private final SlackMessageRepository slackMessageRepository;
    private final SlackApiClient slackApiClient;
    private final SlackMessageDomainService domainService;
    private final HubClient hubClient;
    private final UserClient userClient;
    private final DeliveryClient deliveryClient;

    /**
     * 일반 슬랙 메시지 발송
     *
     * @param request 메시지 요청 DTO
     * @return 저장된 메시지 응답 DTO
     * @throws IllegalArgumentException 메시지 내용이나 수신자 ID가 유효하지 않은 경우
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
     * @see #sendMessage(SlackMessageRequest)
     */
    @Transactional
    public SlackMessageResponse sendDeliveryRouteMessage(SlackMessageRequest request) {
        // TODO: 배송 경로 관련 처리 기능 추가하기
        log.info("배송 경로 메시지 발송. 수신자: {}", request.getReceiverId());
        return sendMessage(request);
    }

    /**
     * 메시지 목록 조회
     *
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬 방식 등)
     * @return 메시지 응답 DTO의 페이지 객체
     */
    @Transactional(readOnly = true)
    public Page<SlackMessageResponse> getMessages(Pageable pageable) {
        Page<SlackMessage> messages = slackMessageRepository.findAllNotDeleted(pageable);
        return messages.map(SlackMessageResponse::from);
    }

    /**
     * 메시지 검색
     *
     * @param searchRequest 검색 요청 DTO (검색 조건 포함)
     * @param pageable      페이징 정보 (페이지 번호, 크기, 정렬 방식 등)
     * @return 검색 결과 메시지 응답 DTO의 페이지 객체
     * @throws RuntimeException 메시지 검색 중 오류가 발생한 경우
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
     * @param size 요청된 페이지 크기
     * @return 검증된 페이지 크기 (10, 30, 50 중 하나, 기본값 10)
     */
    public int validatePageSize(int size) {
        return (size == 10 || size == 30 || size == 50) ? size : 10;
    }

    /**
     * 메시지 상세 조회
     *
     * @param messageId 조회할 메시지 ID
     * @return 메시지 응답 DTO
     * @throws CustomNotFoundException 메시지를 찾을 수 없는 경우
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
     * @param request 일일 배송 알림 요청 DTO (허브 ID, 배송 담당자 ID, 배송 위치 정보 등)
     * @return 일일 배송 알림 응답 DTO (배송 경로 정보 포함)
     */
    @Transactional
    public SlackMessageDailyDeliveryResponse sendDailyDeliveryNotice(
            SlackMessageDailyDeliveryRequest request) {
        log.info("일일 배송 알림 발송 시작. Hub ID: {}, 배송 담당자 ID: {}",
                request.getHubId(), request.getDelivererId());

        DeliverUserDto deliverUser = userClient.getDeliverUserById(request.getDelivererId()).getData();

        HubDto hub = hubClient.getHub(request.getHubId()).getData();

        List<SlackMessageDailyDeliveryResponse.DeliveryRoutePoint> routePoints =
                createDeliveryRoutePoints(request.getDeliveryLocations());

        SlackMessageDailyDeliveryResponse response = SlackMessageDailyDeliveryResponse.builder()
                .hubId(request.getHubId())
                .delivererId(request.getDelivererId())
                .delivererName(deliverUser.getName())
                .slackId(deliverUser.getSlackId())
                .totalDeliveryPoints(routePoints.size())
                .sentAt(LocalDateTime.now().toString())
                .routePoints(routePoints)
                .build();

        String messageContent = generateDeliveryRouteMessage(response);

        boolean isSuccess = slackApiClient.sendMessage(deliverUser.getSlackId(), messageContent);

        SlackMessage message = SlackMessage.builder()
                .receiverId(deliverUser.getSlackId())
                .content(messageContent)
                .status(SlackMessage.MessageStatus.PENDING)
                .build();

        SlackMessage savedMessage = slackMessageRepository.save(message);
        domainService.updateMessageStatus(savedMessage, isSuccess);
        slackMessageRepository.save(savedMessage);

        log.info("일일 배송 알림 발송 완료. 배송 담당자: {}, 방문 지점 수: {}",
                deliverUser.getName(), routePoints.size());

        return response;
    }

    /**
     * 배송 경로 포인트 생성
     *
     * @param locations 배송 위치 정보 목록
     * @return 생성된 배송 경로 포인트 목록
     */
    private List<SlackMessageDailyDeliveryResponse.DeliveryRoutePoint> createDeliveryRoutePoints(
            List<SlackMessageDailyDeliveryRequest.DeliveryLocation> locations) {

        List<SlackMessageDailyDeliveryResponse.DeliveryRoutePoint> routePoints = new ArrayList<>();

        for (int i = 0; i < locations.size(); i++) {
            SlackMessageDailyDeliveryRequest.DeliveryLocation location = locations.get(i);

            SlackMessageDailyDeliveryResponse.DeliveryRoutePoint point =
                    SlackMessageDailyDeliveryResponse.DeliveryRoutePoint.builder()
                            .sequence(i + 1)
                            .destinationId(location.getDestinationId())
                            .destinationName(location.getName())
                            .destinationAddress(location.getAddress())
                            .build();

            routePoints.add(point);
        }

        return routePoints;
    }

    /**
     * 배송 경로 메시지 생성
     *
     * @param response 배송 경로 정보가 포함된 응답 DTO
     * @return 생성된 메시지 내용
     */
    private String generateDeliveryRouteMessage(SlackMessageDailyDeliveryResponse response) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("안녕하세요, ")
                .append(response.getDelivererName())
                .append("님. 오늘의 배송 계획입니다.\n\n");

        messageBuilder.append("배송 순서:\n");
        for (SlackMessageDailyDeliveryResponse.DeliveryRoutePoint point : response.getRoutePoints()) {
            messageBuilder.append(point.getSequence())
                    .append(". ")
                    .append(point.getDestinationName())
                    .append(" (")
                    .append(point.getDestinationAddress())
                    .append(")\n");
        }

        messageBuilder.append("\n총 방문 지점 수: ")
                .append(response.getTotalDeliveryPoints())
                .append("\n\n");

        messageBuilder.append("안전 운행하세요!");

        return messageBuilder.toString();
    }

    /**
     * 정해진 시간에 일일 배송 알림 자동 발송
     *
     * @see #sendDailyDeliveryNotice(SlackMessageDailyDeliveryRequest)
     */
    @Scheduled(cron = "${slack.daily.notification.cron}")
    @Transactional
    public void scheduledDailyDeliveryNotice() {
        log.info("일일 배송 알림 스케줄러 실행");

        try {
            // 1. 배송 담당자별 당일 배송 정보 조회
            // 2. 각 배송 담당자마다 sendDailyDeliveryNotice 호출
            // TODO: 어떻게 구현할지 고민하기
        } catch (Exception e) {
            log.error("일일 배송 알림 스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 특정 배송 담당자의 당일 배송 정보 조회
     *
     * @param delivererId 배송 담당자 ID
     * @param hubId 허브 ID
     * @return 배송 위치 정보 목록
     */
    private List<SlackMessageDailyDeliveryRequest.DeliveryLocation> getDailyDeliveryLocations(
            UUID delivererId, UUID hubId) {

        try {
            // 배송 서비스에서 당일 배송 정보 조회
            List<DeliveryInfoDto> deliveries = deliveryClient.getDailyDeliveries(delivererId, hubId).getData();

            // 배송 정보를 DeliveryLocation으로 변환
            return deliveries.stream()
                    .map(delivery -> SlackMessageDailyDeliveryRequest.DeliveryLocation.builder()
                            .destinationId(delivery.getCompanyId())
                            .name(delivery.getCompanyName())
                            .address(delivery.getCompanyAddress())
                            .deliveryId(delivery.getDeliveryId())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("당일 배송 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 검색 날짜 문자열 파싱
     *
     * @param dateStr 날짜 문자열 (yyyy-MM-dd 또는 yyyy/MM/dd 형식)
     * @param isStartDate 시작 날짜인지 여부 (시작 날짜면 당일 00:00:00, 종료 날짜면 당일 23:59:59 반환)
     * @return 파싱된 LocalDateTime 객체 또는 파싱 실패 시 null
     */
    public LocalDateTime parseDate(String dateStr, boolean isStartDate) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            // 기본 ISO 날짜 형식 (yyyy-MM-dd) 파싱 시도
            LocalDate date = LocalDate.parse(dateStr);
            return isStartDate ? date.atStartOfDay() : date.atTime(23, 59, 59);
        } catch (Exception e) {
            try {
                // 다른 일반적인 날짜 형식 파싱 시도 (yyyy/MM/dd)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDate date = LocalDate.parse(dateStr, formatter);
                return isStartDate ? date.atStartOfDay() : date.atTime(23, 59, 59);
            } catch (Exception e2) {
                log.warn("날짜 형식이 올바르지 않습니다. 형식: yyyy-MM-dd 또는 yyyy/MM/dd: {}", dateStr);
                return null;
            }
        }
    }

    /**
     * 검색 요청에 날짜 조건 설정
     *
     * @param request 검색 요청 객체
     * @param startDateStr 시작 날짜 문자열
     * @param endDateStr 종료 날짜 문자열
     * @see #parseDate(String, boolean)
     */
    public void setSearchDates(SlackMessageSearchRequest request, String startDateStr, String endDateStr) {
        LocalDateTime startDate = parseDate(startDateStr, true);
        if (startDate != null) {
            request.setStartDate(startDate);
        }

        LocalDateTime endDate = parseDate(endDateStr, false);
        if (endDate != null) {
            request.setEndDate(endDate);
        }
    }
}