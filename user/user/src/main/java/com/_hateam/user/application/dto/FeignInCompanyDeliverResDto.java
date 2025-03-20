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
public class FeignInCompanyDeliverResDto {

    private UUID deliverId;
    private Long userId;
    private String slackId;
    private String name;
    private DeliverType deliverType;


    //entity->dto
    public static FeignInCompanyDeliverResDto from(DeliverUser deliverUser) {
        return FeignInCompanyDeliverResDto.builder()
                .deliverId(deliverUser.getDeliverId())
                .userId(deliverUser.getUser().getUserId())
                .slackId(deliverUser.getSlackId())
                .name(deliverUser.getName())
                .deliverType(deliverUser.getDeliverType())
                .build();
    }

}
