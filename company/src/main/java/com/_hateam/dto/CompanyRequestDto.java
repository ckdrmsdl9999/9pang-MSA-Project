package com._hateam.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDto {

    @NotNull(message = "허브 명은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s]{1,20}$", message = "허브 명은 특수문자를 제외한 20자 이하여야 합니다.")
    private String name;

    @NotNull(message = "주소는 필수입니다.")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @NotNull(message = "위도는 필수입니다.")
    @Size(max = 20, message = "위도는 20자 이하여야 합니다.")
    private String latitude;

    @NotNull(message = "경도는 필수입니다.")
    @Size(max = 20, message = "경도는 20자 이하여야 합니다.")
    private String longitude;
}