package com._hateam.order.presentation.controller;

import com._hateam.common.dto.ResponseDto;
import com._hateam.order.application.dto.OrderRequestDto;
import com._hateam.order.application.dto.OrderResponseDto;
import com._hateam.order.application.dto.OrderSearchDto;
import com._hateam.order.application.dto.OrderUpdateDto;
import com._hateam.order.application.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "주문 API", description = "주문 관련 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<ResponseDto<OrderResponseDto>> createOrder(
            @Valid @RequestBody OrderRequestDto requestDto) {
        OrderResponseDto responseDto = orderService.createOrder(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(responseDto));
    }

    @Operation(summary = "주문 조회", description = "주문 ID로 주문을 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderResponseDto>> getOrderById(
            @PathVariable UUID orderId) {
        OrderResponseDto responseDto = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ResponseDto.success(responseDto));
    }

    @Operation(summary = "주문 전체 조회", description = "모든 주문을 조회합니다.")
    @GetMapping
    public ResponseEntity<ResponseDto<List<OrderResponseDto>>> getAllOrders(
            @Parameter(description = "페이지 번호 (1부터 시작)")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "페이지 크기 (10, 30, 50)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "정렬 방향 (asc, desc)")
            @RequestParam(defaultValue = "desc") String sort) {
        List<OrderResponseDto> responseDtos = orderService.getAllOrders(page, size, sort);
        return ResponseEntity.ok(ResponseDto.success(responseDtos));
    }

    @Operation(summary = "주문 검색", description = "주문을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ResponseDto<List<OrderResponseDto>>> searchOrders(
            @ModelAttribute OrderSearchDto searchDto) {
        List<OrderResponseDto> responseDtos = orderService.searchOrders(searchDto);
        return ResponseEntity.ok(ResponseDto.success(responseDtos));
    }

    @Operation(summary = "주문 검색 초기화", description = "주문 검색 조건을 초기화합니다.")
    @GetMapping("/search/reset")
    public ResponseEntity<ResponseDto<String>> resetSearch() {
        return ResponseEntity.ok(ResponseDto.success("주문 검색 초기화 완료"));
    }

    @Operation(summary = "주문 수정", description = "주문을 수정합니다.")
    @PatchMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderResponseDto>> updateOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderUpdateDto updateDto) {
        OrderResponseDto responseDto = orderService.updateOrder(orderId, updateDto);
        return ResponseEntity.ok(ResponseDto.success(responseDto));
    }

    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다.")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseDto<String>> deleteOrder(
            @PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ResponseDto.success("주문 삭제 성공"));
    }
}