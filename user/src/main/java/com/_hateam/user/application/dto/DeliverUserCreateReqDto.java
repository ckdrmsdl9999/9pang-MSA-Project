package com._hateam.user.application.dto;

import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import com._hateam.user.domain.model.DeliverUser;
import com._hateam.user.domain.model.User;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class DeliverUserCreateReqDto {
    private Long userId;
    private UUID hubId;
    private String slackId;
    private String name;
    private DeliverType deliverType;
    private String contactNumber;
    private Status status;

    //dto->entity
    public static DeliverUser toEntity(DeliverUserCreateReqDto deliverUserCreateReqDto,User user) {
        return DeliverUser.builder()
                .deliverId(UUID.randomUUID())
                .user(user).rotationOrder(1)//임시값
                .hubId(deliverUserCreateReqDto.getHubId())
                .slackId(deliverUserCreateReqDto.getSlackId())
                .name(deliverUserCreateReqDto.getName())
                .deliverType(deliverUserCreateReqDto.getDeliverType())
                .contactNumber(deliverUserCreateReqDto.getContactNumber())
                .status(deliverUserCreateReqDto.getStatus())
                .build();

    }

}
