package com._hateam.common.exception;

public class CustomNotFoundException extends RuntimeException {
    public CustomNotFoundException(String message) {
        super(message);
    }
}