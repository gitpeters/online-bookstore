package com.peters.orderservice.service;


import com.peters.orderservice.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<CustomResponse> addBookToCart(Long bookId, Long userId, int quantity);

    ResponseEntity<?> getAllCarts(Long userId);

    ResponseEntity<CustomResponse> getCart(String cartId);

    ResponseEntity<CustomResponse> editCart(String cartId, int quantity);

    ResponseEntity<CustomResponse> deleteCart(String cartId);

    ResponseEntity<?> deleteAllCarts(Long userId);
}
