package com.peters.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InitiatePaymentResponse {
    private String paymentReference;
    private String authorizationUrl;
}
