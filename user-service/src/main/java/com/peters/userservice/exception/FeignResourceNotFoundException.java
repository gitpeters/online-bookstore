package com.peters.userservice.exception;

public class FeignResourceNotFoundException extends RuntimeException {
    public FeignResourceNotFoundException(String message) {
        super(message);
    }
}
