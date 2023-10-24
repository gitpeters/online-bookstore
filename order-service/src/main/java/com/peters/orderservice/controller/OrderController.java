package com.peters.orderservice.controller;

import com.peters.orderservice.dto.CustomResponse;
import com.peters.orderservice.dto.ErrorDetails;
import com.peters.orderservice.dto.FeignErrorHandling;
import com.peters.orderservice.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final FeignErrorHandling feignErrorHandling;
    @GetMapping("/add-to-cart")
    public ResponseEntity<?> addBookToCart(@RequestParam("bookId") Long bookId, @RequestParam("userId") Long userId, @RequestParam("quantity") int quantity){
        try {
            return orderService.addBookToCart(bookId, userId, quantity);
        }catch (FeignException.NotFound e){
           return feignErrorHandling.handleFeignNotFound(e);
        }catch (FeignException e){
            return feignErrorHandling.handleFeignError(e);
        }
    }
    @GetMapping("/{userId}/all-carts")
    public ResponseEntity<?> getAllCarts(@PathVariable("userId") Long userId){
        return orderService.getAllCarts(userId);
    }

}
