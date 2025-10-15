package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.PaymentRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@org.springframework.stereotype.Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final UsersRepository usersRepository;
    private final ServiceRepository serviceRepository;
    private final JwtUtil jwtUtil;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              UsersRepository usersRepository,
                              ServiceRepository serviceRepository,
                              JwtUtil jwtUtil) {
        this.paymentRepository = paymentRepository;
        this.usersRepository = usersRepository;
        this.serviceRepository = serviceRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "paymentsByOwner", allEntries = true),
            @CacheEvict(value = "paymentsByService", allEntries = true),
            @CacheEvict(value = "paymentById", allEntries = true)
    })
    public Payment initiatePayment(String email, Long serviceId) {
        logger.info("Initiating payment for user email: {} and service ID: {}", email, serviceId);

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User with email '{}' not found", email);
                    return new RuntimeException("This owner is not found");
                });

        Long ownerId = user.getId();
        logger.debug("Owner ID resolved: {}", ownerId);

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    logger.error("Service with ID '{}' not found", serviceId);
                    return new RuntimeException("This service does not exist");
                });

        Double amount = service.getPrice();
        Long idOfService = service.getId();
        logger.info("Service details - ID: {}, Amount: {}", idOfService, amount);

        boolean paymentExist;
        Integer paymentUniqueId;

        do {
            Random random = new Random();
            paymentUniqueId = random.nextInt(1000000) + 10000000;

            paymentExist = paymentRepository.findByPaymentId(paymentUniqueId).isPresent();
            if (paymentExist) {
                logger.warn("Generated payment ID {} already exists, regenerating...", paymentUniqueId);
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

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment initiated successfully with Payment ID: {}", savedPayment.getPaymentId());
        return savedPayment;
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "paymentById", key = "#paymentId")
    public Optional<Payment> getPaymentByPaymentId(Integer paymentId) {
        logger.info("Fetching payment by payment ID: {}", paymentId);
        Optional<Payment> payment = paymentRepository.findByPaymentId(paymentId);
        if (payment.isPresent()) {
            logger.debug("Payment found: {}", payment.get());
        } else {
            logger.warn("No payment found for ID: {}", paymentId);
        }
        return payment;
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "paymentsByOwner", key = "#ownerId")
    public List<Payment> getAllPaymentsDoneByOwner(Integer ownerId) {
        logger.info("Fetching all payments made by owner ID: {}", ownerId);
        List<Payment> payments = paymentRepository.findAllByOwnerId(ownerId);
        logger.debug("Total payments found for owner {}: {}", ownerId, payments.size());
        return payments;
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "paymentsByService", key = "#serviceId")
    public List<Payment> getAllPaymentsByService(Long serviceId) {
        logger.info("Fetching all payments for service ID: {}", serviceId);
        List<Payment> payments = paymentRepository.findAllByServiceId(serviceId);
        logger.debug("Total payments found for service {}: {}", serviceId, payments.size());
        return payments;
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "paymentsByOwner", allEntries = true),
            @CacheEvict(value = "paymentsByService", allEntries = true),
            @CacheEvict(value = "paymentById", key = "#paymentId")
    })
    public Payment updatePayment(
            Integer paymentId,
            PaymentStatus paymentStatus,
            String transactionRef,
            PaymentMethod paymentMethod,
            PaymentCurrency paymentCurrency) {

        logger.info("Updating payment with ID: {}", paymentId);

        return paymentRepository.findByPaymentId(paymentId).map(existingPayment -> {
            if (transactionRef != null) existingPayment.setTransactionRef(transactionRef);
            if (paymentCurrency != null) existingPayment.setCurrency(paymentCurrency);
            if (paymentMethod != null) existingPayment.setPaymentMethod(paymentMethod);
            if (paymentStatus != null) existingPayment.setPaymentStatus(paymentStatus);

            existingPayment.setUpdatedAt(String.valueOf(LocalDateTime.now()));

            Payment updatedPayment = paymentRepository.save(existingPayment);
            logger.info("Payment with ID {} updated successfully", paymentId);
            return updatedPayment;
        }).orElseThrow(() -> {
            logger.error("Payment with ID {} not found for update", paymentId);
            return new RuntimeException("Payment not found");
        });
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "paymentsByOwner", allEntries = true),
            @CacheEvict(value = "paymentsByService", allEntries = true),
            @CacheEvict(value = "paymentById", key = "#paymentId")
    })
    public String deletePayment(Integer paymentId) {
        logger.warn("Deleting payment with ID: {}", paymentId);
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment with ID {} not found for deletion", paymentId);
                    return new RuntimeException("Payment not found");
                });
        paymentRepository.delete(payment);
        logger.info("Payment with ID {} deleted successfully", paymentId);
        return "Payment deleted";
    }
}
