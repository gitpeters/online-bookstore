package com.peters.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    private String id;
    private String orderStatus;
    private List<Cart> items;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private Integer totalNumberOfItems;
    private Payment payment;
    private LocalDateTime orderDate;
}
