package com.peters.orderservice.service;

import com.peters.orderservice.dto.CartResponse;
import com.peters.orderservice.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<CustomResponse> addBookToCart(Long bookId, Long userId, int quantity);

    ResponseEntity<?> getAllCarts(Long userId);
}
