package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAllByServiceId(Long requestId);
    List<Payment> findAllByOwnerId(Integer ownerId);
    Optional<Payment> findByPaymentId(Integer paymentId);
}
