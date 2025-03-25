package com._hateam.message.presentation.controller;

import com._hateam.common.dto.PageResponseDto;
import com._hateam.common.dto.ResponseDto;
import com._hateam.message.application.dto.request.SlackMessageDailyDeliveryRequest;
import com._hateam.message.application.dto.request.SlackMessageRequest;
import com._hateam.message.application.dto.request.SlackMessageSearchRequest;
import com._hateam.message.application.dto.response.SlackMessageDailyDeliveryResponse;
import com._hateam.message.application.dto.response.SlackMessageResponse;
import com._hateam.message.application.service.SlackMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/slack")
@RequiredArgsConstructor
@Tag(name = "슬랙 메시지 API", description = "슬랙 메시지 관련 API")
public class SlackMessageController {

    private final SlackMessageService slackMessageService;

    @Operation(summary = "슬랙 메시지 발송", description = "슬랙 메시지를 지정된 수신자에게 발송합니다.")
    @PostMapping("/messages")
    public ResponseDto<SlackMessageResponse> sendMessage(
            @Valid @RequestBody SlackMessageRequest request) {

        log.info("슬랙 메시지 발송 요청. 수신자: {}", request.getReceiverId());
        SlackMessageResponse response = slackMessageService.sendMessage(request);
        return ResponseDto.success(HttpStatus.OK, response);
    }

    @Operation(summary = "배송 경로 메시지 발송", description = "배송 경로 정보가 포함된 슬랙 메시지를 발송합니다.")
    @PostMapping("/messages/delivery-route")
    public ResponseDto<SlackMessageResponse> sendDeliveryRouteMessage(
            @Valid @RequestBody SlackMessageRequest request) {

        log.info("배송 경로 메시지 발송 요청. 수신자: {}", request.getReceiverId());
        SlackMessageResponse response = slackMessageService.sendDeliveryRouteMessage(request);
        return ResponseDto.success(HttpStatus.OK, response);
    }

    @Operation(summary = "슬랙 메시지 목록 조회", description = "전체 슬랙 메시지 목록을 페이징 처리하여 조회합니다.")
    @GetMapping("/messages")
    public PageResponseDto<SlackMessageResponse> getMessages(
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기 (10, 30, 50 중 선택)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "정렬 방향 (asc, desc)")
            @RequestParam(defaultValue = "desc") String sort) {

        size = slackMessageService.validatePageSize(size);

        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        Page<SlackMessageResponse> messages = slackMessageService.getMessages(pageable);
        return PageResponseDto.success(HttpStatus.OK, messages, "메시지 조회 성공");
    }

    @Operation(summary = "슬랙 메시지 검색", description = "여러 검색 조건을 이용하여 슬랙 메시지를 검색합니다.")
    @GetMapping("/messages/search")
    public PageResponseDto<SlackMessageResponse> searchMessages(
            @Parameter(description = "수신자 ID")
            @RequestParam(required = false) String receiverId,

            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "조회 시작일 (yyyy-MM-dd 형식)")
            @RequestParam(required = false) String startDateStr,

            @Parameter(description = "조회 종료일 (yyyy-MM-dd 형식)")
            @RequestParam(required = false) String endDateStr,

            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기 (10, 30, 50 중 선택)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "정렬 방향 (asc, desc)")
            @RequestParam(defaultValue = "desc") String sort) {

        size = slackMessageService.validatePageSize(size);

        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        SlackMessageSearchRequest searchRequest = new SlackMessageSearchRequest();
        searchRequest.setReceiverId(receiverId);
        searchRequest.setKeyword(keyword);

        slackMessageService.setSearchDates(searchRequest, startDateStr, endDateStr);

        Page<SlackMessageResponse> messages = slackMessageService.searchMessages(searchRequest, pageable);
        return PageResponseDto.success(HttpStatus.OK, messages, "메시지 검색 결과");
    }

    @Operation(summary = "슬랙 메시지 상세 조회", description = "메시지 ID를 통해 특정 슬랙 메시지의 상세 정보를 조회합니다.")
    @GetMapping("/messages/{messageId}")
    public ResponseDto<SlackMessageResponse> getMessage(
            @PathVariable UUID messageId) {

        SlackMessageResponse message = slackMessageService.getMessage(messageId);
        return ResponseDto.success(HttpStatus.OK, message);
    }

    @Operation(summary = "일일 배송 알림 발송", description = "지정된 허브의 배송 담당자들에게 일일 배송 알림 메시지를 발송합니다.")
    @PostMapping("/messages/daily-delivery")
    public ResponseDto<SlackMessageDailyDeliveryResponse> sendDailyDeliveryNotice(
            @Valid @RequestBody SlackMessageDailyDeliveryRequest request) {

        log.info("일일 배송 알림 발송 요청. Hub ID: {}, 배송 담당자 ID: {}",
                request.getHubId(), request.getDelivererId());
        SlackMessageDailyDeliveryResponse response = slackMessageService.sendDailyDeliveryNotice(request);
        return ResponseDto.success(HttpStatus.OK, response);
    }
}