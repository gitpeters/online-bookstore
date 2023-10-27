package com.peters.orderservice.controller;


import com.peters.orderservice.dto.CustomResponse;
import com.peters.orderservice.dto.FeignErrorHandling;
import com.peters.orderservice.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<CustomResponse> getCart(@PathVariable("cartId") String cartId){
        return orderService.getCart(cartId);
    }

    @PutMapping("/edit/cart/{cartId}")
    public ResponseEntity<CustomResponse> editCart(@PathVariable("cartId") String cartId, @RequestParam("quantity") int quantity){
        return orderService.editCart(cartId, quantity);
    }

    @DeleteMapping("/delete/cart/{cartId}")
    public ResponseEntity<CustomResponse> deleteCart(@PathVariable("cartId") String cartId){
        return orderService.deleteCart(cartId);
    }

    @DeleteMapping("/{userId}/clear-cart")
    public ResponseEntity<?> deleteAllCarts(@PathVariable("userId") Long userId){
        return orderService.deleteAllCarts(userId);
    }

    @GetMapping("/{userId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable("userId") Long userId, @RequestParam("user_email") String userEmail){
        return orderService.checkOut(userId, userEmail);
    }

    @GetMapping("/confirm-payment")
    public ResponseEntity<CustomResponse> confirmPayment(@RequestParam("referenceId") String paymentReferenceId){
        return orderService.confirmPayment(paymentReferenceId);
    }

}
