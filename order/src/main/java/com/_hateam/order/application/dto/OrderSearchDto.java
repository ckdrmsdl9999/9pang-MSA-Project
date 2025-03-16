package com._hateam.order.application.dto;

import com._hateam.order.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                this.startDate = LocalDate.parse(startDateStr).atStartOfDay();
            } catch (Exception e) {
                this.startDate = null;
            }
        }

        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                this.endDate = LocalDate.parse(endDateStr).atTime(LocalTime.MAX);
            } catch (Exception e) {
                this.endDate = null;
            }
        }
    }
}