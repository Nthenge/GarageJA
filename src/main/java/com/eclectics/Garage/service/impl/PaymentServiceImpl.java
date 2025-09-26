package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.PaymentRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.service.PaymentService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@org.springframework.stereotype.Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UsersRepository usersRepository;
    private final ServiceRepository serviceRepository;
    private final JwtUtil jwtUtil;

    public PaymentServiceImpl(PaymentRepository paymentRepository, UsersRepository usersRepository, ServiceRepository serviceRepository, JwtUtil jwtUtil) {
        this.paymentRepository = paymentRepository;
        this.usersRepository = usersRepository;
        this.serviceRepository = serviceRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    @Override
    public Payment initiatePayment(String email, Long serviceId) {

        User user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("This owner is not found"));
        Long ownerId = user.getId();

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(()-> new RuntimeException("This service does not exist")); // make this dynamic, frontend should select the service, and it should be loaded here automatically using its id

        Double amount = service.getPrice();
        Long idOfService = service.getId();

        boolean paymentExist;
        Integer paymentUniqueId;

        do {
            Random random = new Random();
            paymentUniqueId = random.nextInt(1000000) + 10000000;

            paymentExist = paymentRepository.findByPaymentId(paymentUniqueId).isPresent();
            if (paymentExist){
                throw new RuntimeException("Payment with this id already exist");
            }
        } while (paymentExist);

        Payment payment = new Payment();

        payment.setPaymentId(paymentUniqueId);
        payment.setOwnerId(ownerId);
        payment.setServiceId(idOfService);
        payment.setAmount(amount);
        payment.setCreatedAt(String.valueOf(LocalDateTime.now()));
        payment.setUpdatedAt(null);
        payment.setCurrency(PaymentCurrency.SHS);
        payment.setPaymentMethod(PaymentMethod.M_PESA);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTransactionRef("Mock" + UUID.randomUUID());

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Payment> getPaymentByPaymentId(Integer paymentId) {
        return paymentRepository.findByPaymentId(paymentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Payment> getAllPaymentsDoneByOwner(Integer ownerId) {
        return paymentRepository.findAllByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Payment> getAllPaymentsByService(Long serviceId) {
        return paymentRepository.findAllByServiceId(serviceId);
    }

    @Transactional
    @Override
    public Payment updatePayment(
            Integer paymentId,
            PaymentStatus paymentStatus,
            String transactionRef,
            PaymentMethod paymentMethod,
            PaymentCurrency paymentCurrency) {

        return paymentRepository.findByPaymentId(paymentId).map(existingPayment -> {

            if (transactionRef != null) {
                existingPayment.setTransactionRef(transactionRef);
            }
            if (paymentCurrency != null) {
                existingPayment.setCurrency(paymentCurrency);
            }
            if (paymentMethod != null) {
                existingPayment.setPaymentMethod(paymentMethod);
            }
            if (paymentStatus != null) {
                existingPayment.setPaymentStatus(paymentStatus);
            }

            existingPayment.setUpdatedAt(String.valueOf(LocalDateTime.now()));

            return paymentRepository.save(existingPayment);
        }).orElseThrow(() -> new RuntimeException("Payment not found"));
    }


    @Transactional
    @Override
    public String deletePayment(Integer paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentRepository.delete(payment);
        return "Payment deleted";
    }
}
