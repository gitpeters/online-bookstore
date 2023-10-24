package com.peters.orderservice.repository;

import com.peters.orderservice.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartRepository extends MongoRepository<Cart, String> {
    List<Cart> findByUserId(Long userId);
}
