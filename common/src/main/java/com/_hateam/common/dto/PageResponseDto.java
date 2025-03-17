package com._hateam.common.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class PageResponseDto<T> {

    private int code;
    private String status;
    private String message;
    private List<T> data;
    private PaginationDto paginationDto;

    // 기본 생성자
    public PageResponseDto() {
    }

    // 성공적인 응답을 위한 생성자
    public PageResponseDto(int code, String status, String message, List<T> data, Page page) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
        this.paginationDto = new PaginationDto(page);
    }

    // 실패한 응답을 위한 생성자
    public PageResponseDto(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.paginationDto = null;
        this.data = null;
    }

    public PageResponseDto(HttpStatus httpStatus, List<T> data, Page page) {
        this.code = httpStatus.value();
        this.status = httpStatus.getReasonPhrase();
        this.message = "성공적으로 처리되었습니다.";
        this.data = data;
        this.paginationDto = new PaginationDto(page);
    }

    public PageResponseDto(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.status = httpStatus.getReasonPhrase();
        this.message = message;
        this.data = null;
    }
}