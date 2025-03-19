package com._hateam.feign;

import com._hateam.CompanyType;
import com._hateam.entity.Company;
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
public class CompanyDto {

    @NotNull(message = "회사 ID는 필수입니다.")
    private UUID id;

    @NotNull(message = "소속 허브 ID는 필수입니다.")
    private UUID hubId; // 엔티티의 hub_id와 대응

    @NotNull(message = "관리자 ID는 필수입니다.")
    @Size(max = 50, message = "관리자 ID는 50자 이하여야 합니다.")
    private String userId;

    @NotNull(message = "회사 이름은 필수입니다.")
    @Size(max = 255, message = "회사 이름은 255자 이하여야 합니다.")
    private String companyName;

    @NotNull(message = "회사 주소는 필수입니다.")
    @Size(max = 20, message = "회사 주소는 20자 이하여야 합니다.")
    private String companyAddress;

    @NotNull(message = "회사 타입은 필수입니다.")
    private CompanyType companyType;

    @NotNull(message = "우편번호는 필수입니다.")
    @Size(max = 5, message = "우편번호는 5자 이하여야 합니다.")
    private String postalCode;

    public static CompanyDto companyToCompanyDto(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .hubId(company.getHubId())
                .userId(company.getUserId())
                .companyName(company.getName())
                .companyAddress(company.getAddress())
                .companyType(company.getCompanyType())
                .postalCode(company.getPostalCode())
                .build();
    }
}
