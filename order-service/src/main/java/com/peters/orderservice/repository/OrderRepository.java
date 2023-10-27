package com.peters.orderservice.repository;

import com.peters.orderservice.model.Order;
import com.peters.orderservice.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    @Query(value = "{'payment.paymentReferenceId': ?0}")
    Optional<Order> findByPaymentReferenceId(@Param("paymentReferenceId") String paymentReferenceId);
}
