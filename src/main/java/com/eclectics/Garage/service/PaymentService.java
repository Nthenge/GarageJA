package com.eclectics.Garage.service;

import com.eclectics.Garage.model.Payment;
import com.eclectics.Garage.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment initiatePayment(String email, Long serviceId);
    Optional<Payment> getPaymentByPaymentId(Integer paymentId);
    List<Payment> getAllPaymentsDoneByOwner(Integer ownerId);
    List<Payment>  getAllPaymentsByService(Long serviceId);
    Payment updatePayment(Integer paymentId, PaymentStatus paymentStatus, String transactionRef);
    String deletePayment(Integer paymentId);
}
