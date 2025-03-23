package com._hateam.user.application.dto;

import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import com._hateam.user.domain.model.DeliverUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverUserResponseDto {

    private UUID deliverId;
    private UUID hubId;
    private Long userId;
    private String slackId;
    private String name;
    private DeliverType deliverType;
    private String contactNumber;
    private Integer rotationOrder;
    private Status status;

    //entity->dto
    public static DeliverUserResponseDto from(DeliverUser deliverUser) {
        return DeliverUserResponseDto.builder()
                .deliverId(deliverUser.getDeliverId())
                .hubId(deliverUser.getHubId())
                .userId(deliverUser.getUser().getUserId())
                .slackId(deliverUser.getSlackId())
                .name(deliverUser.getName())
                .deliverType(deliverUser.getDeliverType())
                .contactNumber(deliverUser.getContactNumber())
                .rotationOrder(deliverUser.getRotationOrder())
                .status(deliverUser.getStatus())
                .build();
    }

}
