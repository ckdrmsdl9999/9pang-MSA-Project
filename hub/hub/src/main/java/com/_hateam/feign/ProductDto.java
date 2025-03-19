package com._hateam.feign;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    @NotNull(message = "상품 ID는 필수입니다.")
    private UUID id;

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

    public static ProductDto productToProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .companyId(product.getCompany().getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
