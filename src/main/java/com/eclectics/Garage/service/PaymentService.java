package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.PaymentsResponseDTO;
import com.eclectics.Garage.model.PaymentCurrency;
import com.eclectics.Garage.model.PaymentMethod;
import com.eclectics.Garage.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    PaymentsResponseDTO initiatePayment(String email, Long serviceId);
    Optional<PaymentsResponseDTO> getPaymentByPaymentId(Integer paymentId);
    List<PaymentsResponseDTO> getAllPaymentsDoneByOwner(Integer ownerId);
    List<PaymentsResponseDTO>  getAllPaymentsByService(Long serviceId);
    PaymentsResponseDTO updatePayment(Integer paymentId, PaymentStatus paymentStatus, String transactionRef, PaymentMethod paymentMethod, PaymentCurrency paymentCurrency);
    void deletePayment(Integer paymentId);
}
