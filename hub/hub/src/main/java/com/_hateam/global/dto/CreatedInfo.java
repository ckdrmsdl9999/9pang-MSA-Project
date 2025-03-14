package com._hateam.global.dto;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

// 인증 정보를 담을 DTO
@Getter
public class CreatedInfo {
    private final String createdBy;
    private final LocalDateTime createdAt;

    public CreatedInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        this.createdBy = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : "anonymous";
        this.createdAt = LocalDateTime.now();
    }

}

