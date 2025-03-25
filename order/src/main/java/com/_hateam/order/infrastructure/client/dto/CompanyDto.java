package com._hateam.order.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private UUID id;
    private UUID hubId;
    private String userId;
    private String companyName;
    private String companyAddress;
    private String companyType;
    private String postalCode;
}