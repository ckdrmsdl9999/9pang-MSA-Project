package com._hateam.delivery.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter // todo: 임시객체 생성을 위해 사용중, 나중에 삭제할것
public class UserClientDeliverResponseDto {
    private UUID deliverId; //todo: username or UUID
    private String slackId;
}
