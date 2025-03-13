package com._hateam.global.exception;

import jakarta.validation.ConstraintViolation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private String errorMessage;
    private int statusCode;
    Object data;
    List<ValidationError> errors;

    //dto 필드 유효성 검사
    public static ExceptionResponse of(BindingResult bindingResult) {
        return new ExceptionResponse ("Validation Error", 400 , null,
            ValidationError.ofFieldErrors(bindingResult.getFieldErrors()));
    }

    //파라미터 유효성 검사
    public static ExceptionResponse of(Set<ConstraintViolation<?>> violations) {
        return new ExceptionResponse( "Constraint Violation", 400 , null,
            ValidationError.ofConstraintViolations(violations));
    }

    public static ExceptionResponse of(String errorMessage, int statusCode) {
        return new ExceptionResponse(errorMessage, statusCode, null, null);
    }

    public static ExceptionResponse of(String errorMessage, int statusCode, Object data) {
        return new ExceptionResponse(errorMessage, statusCode, data, null);
    }

    @Getter
    @AllArgsConstructor
    public static class ValidationError {

        private String field;
        private Object rejectedValue;
        private String reason;

        public static List<ValidationError> ofFieldErrors(
			List<org.springframework.validation.FieldError> fieldErrors) {
            return fieldErrors.stream()
                .map(error -> new ValidationError(
                    error.getField(),
                    error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                    error.getDefaultMessage()))
                .toList();
        }

        public static List<ValidationError> ofConstraintViolations(
			Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                .map(violation -> new ValidationError(
                    violation.getPropertyPath().toString(),
                    violation.getInvalidValue().toString(),
                    violation.getMessage()))
                .toList();
        }
    }
}
