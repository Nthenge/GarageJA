package com.eclectics.Garage.service;

import com.eclectics.Garage.model.Payment;
import com.eclectics.Garage.model.PaymentCurrency;
import com.eclectics.Garage.model.PaymentMethod;
import com.eclectics.Garage.model.PaymentStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment initiatePayment(String email, Long serviceId);
    Optional<Payment> getPaymentByPaymentId(Integer paymentId);
    List<Payment> getAllPaymentsDoneByOwner(Integer ownerId);
    List<Payment>  getAllPaymentsByService(Long serviceId);
    Payment updatePayment(Integer paymentId, PaymentStatus paymentStatus, String transactionRef, PaymentMethod paymentMethod, PaymentCurrency paymentCurrency);
    String deletePayment(Integer paymentId);
}
