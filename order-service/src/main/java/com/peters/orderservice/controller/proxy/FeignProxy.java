package com.peters.orderservice.controller.proxy;

import com.peters.orderservice.dto.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "book-service", url = "http://localhost:8080/api/v1/book")
public interface FeignProxy {
    @GetMapping("/{bookId}")
    ResponseEntity<CustomResponse> getBookById(@PathVariable("bookId") Long bookId);
}
