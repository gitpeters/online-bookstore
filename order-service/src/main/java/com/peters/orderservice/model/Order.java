package com.peters.orderservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private Long bookId;
    private Long userId;
    private String orderStatus;
    private String paymentStatus;
    private LocalDateTime orderDate;
}
