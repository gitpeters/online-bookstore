package com.peters.orderservice.controller.proxy;

import com.peters.orderservice.dto.CustomResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "paystack", url = "https://api.paystack.co")
@Headers("Content-Type: application/json")
public interface PaystackFeignProxy {

    @PostMapping("transaction/initialize")
    ResponseEntity<CustomResponse> initiatePayment(@RequestHeader("Authorization") String authorizationToken, @RequestBody Map<String, String> data);

    @GetMapping("/transaction/verify/{reference}")
    ResponseEntity<CustomResponse> confirmPayment(@RequestHeader("Authorization") String authorizationToken, @PathVariable("reference")String referenceId);
}
