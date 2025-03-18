package com._hateam;

import lombok.Getter;

@Getter
public enum CompanyType {
    PRODUCE("생산 업체"),
    RECEIVE("수령 업체");

    private final String companyType;

    CompanyType(String companyType) {
        this.companyType = companyType;
    }
}

