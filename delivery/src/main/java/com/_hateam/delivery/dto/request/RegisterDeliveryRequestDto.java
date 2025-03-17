package com._hateam.delivery.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
@Valid
public class RegisterDeliveryRequestDto {
    /**
     * todo: 고민할 사항 - 겨우 하나 받아서 쓸건데 requestbody가 맞는가?
     */
    @NotNull(message = "주문id는 필수 입력 값입니다.")
    private UUID orderId;
}
