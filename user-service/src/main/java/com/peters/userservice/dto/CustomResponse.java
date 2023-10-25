package com.peters.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse {
    private String status;
    private Object data;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private Integer totalNumberOfItems;
    private String message;

    public CustomResponse(HttpStatus status, String message) {
        this.status = (status.is2xxSuccessful() ? "success" : "error");
        this.message = message;
    }

    public CustomResponse(String status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
}
