package com._hateam.user.application.dto;

import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverUserUpdateReqDto {
    private UUID hubId;
    private String slackId;
    private String name;
    private DeliverType deliverType;
    private String contactNumber;
    private Integer rotationOrder;
    private Status status;







}
