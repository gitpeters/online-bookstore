package com.peters.userservice.controller.proxy;

import com.peters.userservice.dto.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service", url = "http://localhost:8081/api/v1/orders")
public interface OrderFeignProxy {
    @GetMapping("/add-to-cart")
    ResponseEntity<CustomResponse> addBookToCart(@RequestParam("bookId") Long bookId, @RequestParam("userId") Long userId, @RequestParam("quantity") int quantity);

    @GetMapping("/{userId}/all-carts")
    ResponseEntity<CustomResponse> getAllCarts(@PathVariable("userId") Long userId);

    @GetMapping("/cart/{cartId}")
    ResponseEntity<CustomResponse> getCart(@PathVariable("cartId") String cartId);

    @PutMapping("/edit/cart/{cartId}")
    ResponseEntity<CustomResponse> editCart(@PathVariable("cartId") String cartId, @RequestParam("quantity") int quantity);

    @DeleteMapping("/delete/cart/{cartId}")
    ResponseEntity<CustomResponse> deleteCart(@PathVariable("cartId") String cartId);

    @DeleteMapping("/{userId}/clear-cart")
    ResponseEntity<?> deleteAllCarts(@PathVariable("userId") Long userId);

    @GetMapping("/{userId}/checkout")
    ResponseEntity<?> checkout(@PathVariable("userId") Long userId, @RequestParam("user_email") String userEmail);

    @GetMapping("/confirm-payment")
    ResponseEntity<CustomResponse> confirmPayment(@RequestParam("referenceId") String paymentReferenceId);
}