package com.peters.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private Long id;
    private Long bookId;
    private Long userId;
    private String bookName;
    private String authorName;
    private Integer quantity;
    private BigDecimal bookPrice;
    private BigDecimal subTotal;
    private LocalDate dateAdded;
}
