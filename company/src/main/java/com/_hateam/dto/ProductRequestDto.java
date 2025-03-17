package com._hateam.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotNull(message = "소속 회사 ID는 필수입니다.")
    private UUID companyId;

    @NotNull(message = "상품 이름은 필수입니다.")
    @Size(max = 50, message = "상품 이름은 50자 이하여야 합니다.")
    private String name;

    @NotNull(message = "수량은 필수입니다.")
    private Integer quantity;

    @NotNull(message = "상품 설명은 필수입니다.")
    @Size(max = 255, message = "상품 설명은 255자 이하여야 합니다.")
    private String description;

    @NotNull(message = "가격은 필수입니다.")
    private Integer price;
}
