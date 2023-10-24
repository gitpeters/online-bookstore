package com.peters.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private String feignErrorMessage;

    public ErrorDetails(LocalDateTime timestamp, String message, String details) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
    public ErrorDetails(LocalDateTime timestamp, String message, String details, String feignErrorMessage) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.feignErrorMessage = feignErrorMessage;
    }

    public ErrorDetails(String message, String details) {
        this.message = message;
        this.details = details;
    }
}
