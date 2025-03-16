package com._hateam.order.application.dto;

import com._hateam.order.domain.model.Order;
import com._hateam.order.domain.model.OrderProduct;
import com._hateam.order.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private UUID orderId;
    private UUID deliverId;
    private UUID hubId;
    private UUID companyId;
    private String orderRequest;
    private LocalDateTime deliveryDeadline;
    private OrderStatus status;
    private Integer totalPrice;
    private List<OrderProductDto> products;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProductDto {
        private UUID orderProductId;
        private UUID productId;
        private Integer totalQuantity;
        private Integer totalPrice;
    }

    public static OrderResponseDto from(Order order) {
        List<OrderProductDto> productDtos = order.getOrderProducts().stream()
                .map(OrderResponseDto::convertToDto)
                .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .deliverId(order.getDeliverId())
                .hubId(order.getHubId())
                .companyId(order.getCompanyId())
                .orderRequest(order.getOrderRequest())
                .deliveryDeadline(order.getDeliveryDeadline())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .products(productDtos)
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy())
                .updatedAt(order.getUpdatedAt())
                .updatedBy(order.getUpdatedBy())
                .build();
    }

    private static OrderProductDto convertToDto(OrderProduct orderProduct) {
        return OrderProductDto.builder()
                .orderProductId(orderProduct.getOrderProductId())
                .productId(orderProduct.getProductId())
                .totalQuantity(orderProduct.getTotalQuantity())
                .totalPrice(orderProduct.getTotalPrice())
                .build();
    }
}