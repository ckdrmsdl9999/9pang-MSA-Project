package com._hateam.delivery.dto.request;

import com._hateam.delivery.entity.DeliveryStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
@Valid
public class UpdateDeliveryStautsRequestDto {
    @Pattern(regexp =
            "^(WAITING_AT_HUB|MOVING_TO_HUB|ARRIVED_AT_DEST_HUB|OUT_FOR_DELIVERY|MOVING_TO_COMPANY|DELIVERY_COMPLETED;)$",
            message = "유효하지 않은 상태값입니다.")
    private DeliveryStatus status;
}
