package com._hateam.order.application.dto;

import com._hateam.order.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchDto {

    private String searchTerm;
    private OrderStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
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
        return size == null || size < 1 ? 10 : Math.min(size, 50);
    }

    public String getSort() {
        return sort == null || (!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc"))
                ? "desc" : sort.toLowerCase();
    }
}