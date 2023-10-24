package com.peters.userservice.controller.proxy;

import com.peters.userservice.dto.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "http://localhost:8081/api/v1/orders")
public interface OrderFeignProxy {
    @GetMapping("/add-to-cart")
    ResponseEntity<CustomResponse> addBookToCart(@RequestParam("bookId") Long bookId, @RequestParam("userId") Long userId, @RequestParam("quantity") int quantity);
}
