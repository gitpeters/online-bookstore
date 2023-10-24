package com.peters.orderservice.controller;

import com.peters.orderservice.dto.CustomResponse;
import com.peters.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping("/add-to-cart")
    public ResponseEntity<CustomResponse> addBookToCart(@RequestParam("bookId") Long bookId, @RequestParam("userId") Long userId, @RequestParam("quantity") int quantity){
        return orderService.addBookToCart(bookId, userId, quantity);
    }

}
