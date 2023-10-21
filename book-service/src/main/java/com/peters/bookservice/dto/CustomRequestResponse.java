package com.peters.bookservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class CustomRequestResponse {
    private String status;
    private Object data;
    private String message;

    public CustomRequestResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
