package com._hateam.delivery.dto.request;

import com._hateam.delivery.entity.DeliveryStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.UUID;

@Getter
@Valid
public class UpdateDeliveryRequestDto {

    @NotNull(message = "배송정보의 id는 필수 입력 값입니다.")
    private UUID id;

    @NotNull(message = "주문id는 필수 입력 값입니다.")
    private UUID orderId;

    @Pattern(regexp =
            "^(WAITING_AT_HUB|MOVING_TO_HUB|ARRIVED_AT_DEST_HUB|OUT_FOR_DELIVERY|MOVING_TO_COMPANY|DELIVERY_COMPLETED;)$",
            message = "유효하지 않은 상태값입니다.")
    private DeliveryStatus status;

    @NotNull(message = "시작허브id는 필수 입력 값입니다.")
    private UUID startHubId;

    @NotNull(message = "도착허브id는 필수 입력 값입니다.")
    private UUID endHubId;

    @NotBlank(message = "수령주소는 필수 입력 값입니다.")
    private String receiverAddress;

    @NotBlank(message = "수령인명은 필수 입력 값입니다.")
    private String receiverName;

    @NotBlank(message = "수령인 슬랙id는 필수 입력 값입니다.")
    private String receiverSlackId;

    @NotBlank(message = "배송담당자명은 필수 입력 값입니다.")
    private String delivererUsername;
}
