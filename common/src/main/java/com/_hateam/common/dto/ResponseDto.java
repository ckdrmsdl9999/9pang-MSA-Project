package com._hateam.common.dto;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;

public class ResponseDto<T> {

    private int code;
    private String status;
    private String message;
    private T data;

    // 기본 생성자
    public ResponseDto() {
    }

    // 성공적인 응답을 위한 생성자
    public ResponseDto(int code, String status, String message, T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // 실패한 응답을 위한 생성자
    public ResponseDto(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public ResponseDto(HttpStatus httpStatus, T data) {
        this.code = httpStatus.value();
        this.status = httpStatus.getReasonPhrase();
        this.message = "성공적으로 처리되었습니다.";
        this.data = data;
    }

    public ResponseDto(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.status = httpStatus.getReasonPhrase();
        this.message = message;
        this.data = null;
    }

    // Getter, Setter
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // 성공 응답을 쉽게 반환할 수 있는 메서드
    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(200, "success", "성공적으로 처리되었습니다.", data);
    }

    public static <T> ResponseDto<T> success(HttpStatus status, T data) {
        return new ResponseDto<>(status, data);
    }

    // 실패 응답을 쉽게 반환할 수 있는 메서드
    public static ResponseDto<Object> failure(HttpStatus status, String message) {
        return new ResponseDto<>(status, message);
    }

    // uri location을 담은 응답을 위한 메서드
    public static ResponseDto<Void> responseWithLocation(HttpStatus status, URI location, String message) {
        // 현재 HttpServletResponse 객체를 가져옴
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletResponse response = requestAttributes.getResponse();
            if (response != null) {
                // Location 헤더에 URI를 추가
                response.setHeader("Location", location.toString());
            }
        }
        return new ResponseDto<>(status, message);
    }

    public static ResponseDto<Void> responseWithNoData(HttpStatus status, String message) {
        return new ResponseDto<>(status, message);
    }
}