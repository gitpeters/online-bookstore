package com.peters.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.peters.orderservice.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private String status;
    private Cart cart;
    private BigDecimal totalPrice;
    private Integer totalQuantity;
}
