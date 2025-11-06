package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.PaymentsResponseDTO;
import com.eclectics.Garage.mapper.PaymentsMapper;
import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.PaymentRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.service.PaymentService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final UsersRepository usersRepository;
    private final ServiceRepository serviceRepository;
    private final JwtUtil jwtUtil;
    private final PaymentsMapper mapper;

    private final Map<Integer, Payment> inMemoryPaymentCache = new ConcurrentHashMap<>();
    private final Set<Integer> existingPaymentIds = Collections.synchronizedSet(new HashSet<>());
    private final Map<Long, List<Payment>> ownerPaymentsMap = new ConcurrentHashMap<>();
    private final Map<Long, List<Payment>> servicePaymentsMap = new ConcurrentHashMap<>();

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              UsersRepository usersRepository,
                              ServiceRepository serviceRepository,
                              JwtUtil jwtUtil, PaymentsMapper mapper) {
        this.paymentRepository = paymentRepository;
        this.usersRepository = usersRepository;
        this.serviceRepository = serviceRepository;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;

        paymentRepository.findAll().forEach(payment -> existingPaymentIds.add(payment.getPaymentId()));
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "paymentsByOwner", allEntries = true),
            @CacheEvict(value = "paymentsByService", allEntries = true),
            @CacheEvict(value = "paymentById", allEntries = true)
    })
    public PaymentsResponseDTO initiatePayment(String email, Long serviceId) {
        logger.info("Initiating payment for user email: {} and service ID: {}", email, serviceId);

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("This owner is not found"));
        Long ownerId = user.getId();

        Service service = (Service) serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("This service does not exist"));

        Double amount = ((com.eclectics.Garage.model.Service) service).getPrice();

        Random random = new Random();
        Integer paymentUniqueId;
        do {
            paymentUniqueId = random.nextInt(1000000) + 10000000;
        } while (existingPaymentIds.contains(paymentUniqueId));

        existingPaymentIds.add(paymentUniqueId);

        Payment payment = new Payment();
        payment.setPaymentId(paymentUniqueId);
        payment.setOwnerId(ownerId);
        payment.setServiceId(((com.eclectics.Garage.model.Service) service).getId());
        payment.setAmount(amount);
        payment.setCreatedAt(String.valueOf(LocalDateTime.now()));
        payment.setUpdatedAt(null);
        payment.setCurrency(PaymentCurrency.SHS);
        payment.setPaymentMethod(PaymentMethod.M_PESA);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTransactionRef("Mock" + UUID.randomUUID());

        Payment savedPayment = paymentRepository.save(payment);

        inMemoryPaymentCache.put(paymentUniqueId, savedPayment);
        ownerPaymentsMap.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(savedPayment);
        servicePaymentsMap.computeIfAbsent(serviceId, k -> new ArrayList<>()).add(savedPayment);

        logger.info("Payment initiated successfully with Payment ID: {}", savedPayment.getPaymentId());
        return mapper.toResponseDTO(savedPayment);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "paymentById", key = "#paymentId")
    public Optional<PaymentsResponseDTO> getPaymentByPaymentId(Integer paymentId) {
        logger.info("Fetching payment by payment ID: {}", paymentId);

        if (inMemoryPaymentCache.containsKey(paymentId)) {
            return Optional.of(mapper.toResponseDTO(inMemoryPaymentCache.get(paymentId)));
        }

        Optional<Payment> payment = paymentRepository.findByPaymentId(paymentId);
        payment.ifPresent(p -> inMemoryPaymentCache.put(paymentId, p));

        return mapper.toOptionalResponse(payment);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "paymentsByOwner", key = "#ownerId")
    public List<PaymentsResponseDTO> getAllPaymentsDoneByOwner(Integer ownerId) {
        logger.info("Fetching all payments made by owner ID: {}", ownerId);

        List<Payment> payments = ownerPaymentsMap.getOrDefault(ownerId.longValue(), Collections.emptyList());
        if (payments.isEmpty()) {
            payments = paymentRepository.findAllByOwnerId(ownerId);
            ownerPaymentsMap.put(ownerId.longValue(), payments);
        }
        return mapper.toResponseDTOList(payments);
    }

    @Override
    public List<Payment> getAllPayments() {
        if (!inMemoryPaymentCache.isEmpty()) {
            return new ArrayList<>(inMemoryPaymentCache.values());
        }
        List<Payment> all = paymentRepository.findAll();
        all.forEach(p -> inMemoryPaymentCache.put(p.getPaymentId(), p));
        return all;
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "paymentsByService", key = "#serviceId")
    public List<PaymentsResponseDTO> getAllPaymentsByService(Long serviceId) {
        logger.info("Fetching all payments for service ID: {}", serviceId);

        List<Payment> payments = servicePaymentsMap.getOrDefault(serviceId, Collections.emptyList());
        if (payments.isEmpty()) {
            payments = paymentRepository.findAllByServiceId(serviceId);
            servicePaymentsMap.put(serviceId, payments);
        }
        return mapper.toResponseDTOList(payments);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "paymentsByOwner", allEntries = true),
            @CacheEvict(value = "paymentsByService", allEntries = true),
            @CacheEvict(value = "paymentById", key = "#paymentId")
    })
    public PaymentsResponseDTO updatePayment(
            Integer paymentId,
            PaymentStatus paymentStatus,
            String transactionRef,
            PaymentMethod paymentMethod,
            PaymentCurrency paymentCurrency) {

        logger.info("Updating payment with ID: {}", paymentId);

        Payment payment = inMemoryPaymentCache.getOrDefault(paymentId,
                paymentRepository.findByPaymentId(paymentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found")));

        if (transactionRef != null) payment.setTransactionRef(transactionRef);
        if (paymentCurrency != null) payment.setCurrency(paymentCurrency);
        if (paymentMethod != null) payment.setPaymentMethod(paymentMethod);
        if (paymentStatus != null) payment.setPaymentStatus(paymentStatus);

        payment.setUpdatedAt(String.valueOf(LocalDateTime.now()));
        Payment updatedPayment = paymentRepository.save(payment);

        inMemoryPaymentCache.put(paymentId, updatedPayment);

        logger.info("Payment with ID {} updated successfully", paymentId);
        return mapper.toResponseDTO(updatedPayment);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "paymentsByOwner", allEntries = true),
            @CacheEvict(value = "paymentsByService", allEntries = true),
            @CacheEvict(value = "paymentById", key = "#paymentId")
    })
    public void deletePayment(Integer paymentId) {
        logger.warn("Deleting payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        paymentRepository.delete(payment);

        inMemoryPaymentCache.remove(paymentId);
        existingPaymentIds.remove(paymentId);

        ownerPaymentsMap.computeIfPresent(payment.getOwnerId(), (k, v) -> {
            v.remove(payment);
            return v;
        });
        servicePaymentsMap.computeIfPresent(payment.getServiceId(), (k, v) -> {
            v.remove(payment);
            return v;
        });

        logger.info("Payment with ID {} deleted successfully", paymentId);
    }
}
