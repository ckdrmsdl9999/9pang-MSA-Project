package com._hateam.order.application.dto;

import com._hateam.order.domain.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDto {

    private UUID deliverId;
    private UUID hubId;
    private UUID companyId;
    private String orderRequest;

    @FutureOrPresent(message = "배송 마감일은 현재 이후 날짜여야 합니다.")
    private LocalDateTime deliveryDeadline;

    private OrderStatus status;

    @Valid
    private List<OrderProductDto> products;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProductDto {
        @NotNull(message = "상품 ID는 필수 입력값입니다.")
        private UUID productId;

        @NotNull(message = "수량은 필수 입력값입니다.")
        private Integer quantity;

        @NotNull(message = "가격은 필수 입력값입니다.")
        private Integer totalPrice;
    }
}