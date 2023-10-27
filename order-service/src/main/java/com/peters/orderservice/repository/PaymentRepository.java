package com.peters.orderservice.repository;

import com.peters.orderservice.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByPaymentReferenceId(String paymentReferenceId);
}
