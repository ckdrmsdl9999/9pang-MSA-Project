package com._hateam.order.application.dto;

import com._hateam.order.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchDto {

    private String searchTerm;
    private OrderStatus status;
    private String startDateStr;
    private String endDateStr;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UUID companyId;
    private UUID hubId;
    private Integer page;
    private Integer size;
    private String sort;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String getSearchTerm() {
        return searchTerm == null ? "" : searchTerm;
    }

    public Integer getPage() {
        return page == null || page < 1 ? 1 : page;
    }

    public Integer getSize() {
        if (size == null || size <= 0) {
            return 10;
        } else if (size <= 10) {
            return 10;
        } else if (size <= 30) {
            return 30;
        } else {
            return 50;
        }
    }

    public String getSort() {
        return sort == null || (!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc"))
                ? "desc" : sort.toLowerCase();
    }

    public void processDateStrings() {
        // 시작일 처리
        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(startDateStr.trim(), DATE_FORMATTER);
                this.startDate = date.atStartOfDay();
            } catch (DateTimeParseException e) {
                this.startDate = null;
            }
        } else {
            this.startDate = null;
        }

        // 종료일 처리
        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(endDateStr.trim(), DATE_FORMATTER);
                // 종료일은 해당 일의 마지막 시간(23:59:59.999999999)으로 설정
                this.endDate = date.atTime(LocalTime.MAX);
            } catch (DateTimeParseException e) {
                this.endDate = null;
            }
        } else {
            this.endDate = null;
        }
    }
}