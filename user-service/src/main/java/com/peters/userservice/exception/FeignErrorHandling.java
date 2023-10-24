package com.peters.userservice.exception;

import com.peters.userservice.dto.CustomResponse;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FeignErrorHandling {

    public ResponseEntity<?> handleFeignNotFound(FeignException.NotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Resource not found.", "The requested resource was not found.", e));
    }

    public ResponseEntity<?> handleFeignError(FeignException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Request processing error.", "An error occurred while processing the request.", e));
    }

    public ResponseEntity<?> handleFeignNullResponseError(ResponseEntity<CustomResponse> response) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(createErrorResponseForNullValue("Resource not found.", "The requested resource was not found."));
    }

    private ErrorDetails createErrorResponse(String message, String details, FeignException e) {
        ErrorDetails errorResponse = new ErrorDetails();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setMessage(message);
        errorResponse.setDetails(details);
        errorResponse.setFeignErrorMessage(e.getMessage().toString());
        return errorResponse;
    }
    private ErrorDetails createErrorResponseForNullValue(String message, String details) {
        ErrorDetails errorResponse = new ErrorDetails();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setMessage(message);
        errorResponse.setDetails(details);
        return errorResponse;
    }
}
