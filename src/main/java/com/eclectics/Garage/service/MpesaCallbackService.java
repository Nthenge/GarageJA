package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.dto.MpesaCallbackDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.exception.GarageExceptions;
import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.PaymentRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.repository.UsersRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@org.springframework.stereotype.Service
public class MpesaCallbackService {

    private final ServiceRepository serviceRepository;
    private final GarageRepository garageRepository;
    private final PaymentSplitService paymentSplitService;
    private final MpesaB2BService mpesaBBCService;
    private final PaymentRepository paymentRepository;
    private final UsersRepository usersRepository;

    public MpesaCallbackService(ServiceRepository serviceRepository,
                                GarageRepository garageRepository,
                                PaymentSplitService paymentSplitService, MpesaB2BService mpesaBBCService, PaymentRepository paymentRepository, UsersRepository usersRepository) {
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
        this.paymentSplitService = paymentSplitService;
        this.mpesaBBCService = mpesaBBCService;
        this.paymentRepository = paymentRepository;
        this.usersRepository = usersRepository;
    }

    public void handleCallback(MpesaCallbackDTO callback) {
        var stk = callback.getBody().getStkCallback();

        System.out.println("✅ M-Pesa Callback Received");
        System.out.println("ResultCode: " + stk.getResultCode());
        System.out.println("ResultDesc: " + stk.getResultDesc());

        if (stk.getResultCode() == 0) {
            // Parse callback metadata
            var metadata = stk.getCallbackMetadata().getItem();
            String amountStr = null;
            String receipt = null;
            String phone = null;
            for (var item : metadata) {
                switch (item.getName()) {
                    case "Amount" -> amountStr = item.getValue().toString();
                    case "MpesaReceiptNumber" -> receipt = item.getValue().toString();
                    case "PhoneNumber" -> phone = item.getValue().toString();
                }
            }
            Double amount = Double.parseDouble(amountStr);

            // Fetch service and garage
            ServiceResponseDTO service = getServiceFromCallback(callback);
            GarageResponseDTO garage = getGarageFromService(service);

            // Split payment
            PaymentSplitService.PaymentSplitResult split = paymentSplitService.calculateSplit(service, garage);

            // Send money to garage and system
            try {
                // 1. Send Garage's Share (Till to Till)
                mpesaBBCService.transferMoney(
                        split.getGarageAmount(),
                        String.valueOf(split.getGarageTill()), // Ensure it's a String
                        "Garage service payment",
                        "BusinessBuyGoods" // Use the appropriate B2B command ID for Tills
                );

                // 2. Send System's Commission (Till to Till)
                mpesaBBCService.transferMoney(
                        split.getSystemAmount(),
                        split.getSystemTill(), // This is already a String (600000)
                        "System commission split",
                        "BusinessBuyGoods" // Use the appropriate B2B command ID
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ✅ Save Payment record
            Payment payment = new Payment();
            payment.setAmount(amount);
            payment.setGarageAmount(Double.parseDouble(split.getGarageAmount()));
            payment.setSystemAmount(Double.parseDouble(split.getSystemAmount()));
            payment.setOwnerId(findOwnerIdByPhone(phone)); // implement this method to get carOwnerId
            payment.setServiceId(service.getId());
            payment.setTransactionRef(receipt);
            payment.setCurrency(PaymentCurrency.SHS);
            payment.setPaymentMethod(PaymentMethod.M_PESA);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setCreatedAt(currentTimestamp());
            payment.setUpdatedAt(currentTimestamp());

            paymentRepository.save(payment);

        } else {
            System.out.println("Payment failed or cancelled: " + stk.getResultDesc());
        }
    }


    private Long findOwnerIdByPhone(String phone) {
        // Remove country code formatting if necessary, e.g., +254
        String formattedPhone = phone.startsWith("+") ? phone.substring(1) : phone;

        var carOwner = usersRepository.findByPhoneNumber(formattedPhone)
                .orElseThrow(() -> new RuntimeException("Car owner not found for phone: " + phone));

        return carOwner.getId();
    }

    private String currentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public ServiceResponseDTO getServiceFromCallback(MpesaCallbackDTO callback) {
        String checkoutRequestId = callback.getBody().getStkCallback().getCheckoutRequestID();

        // Expecting format: "serviceId:garageId"
        String[] parts = checkoutRequestId.split(":");
        if (parts.length != 2) {
            throw new GarageExceptions.ResourceNotFoundException(
                    "Invalid CheckoutRequestID format, expected serviceId:garageId"
            );
        }

        Long serviceId = Long.parseLong(parts[0]);
        Long garageId = Long.parseLong(parts[1]);

        // Fetch service
        Service serviceEntity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new GarageExceptions.ResourceNotFoundException(
                        "Service not found for ID: " + serviceId
                ));

        // Find the specific garage linked to this service
        Garage linkedGarage = serviceEntity.getGarages().stream()
                .filter(g -> g.getId().equals(garageId))
                .findFirst()
                .orElseThrow(() -> new GarageExceptions.ResourceNotFoundException(
                        "Garage not found for this service"
                ));

        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setId(serviceEntity.getId());
        dto.setServiceName(serviceEntity.getServiceName());
        dto.setPrice(serviceEntity.getPrice());
        dto.setGarageId(linkedGarage.getId());

        return dto;
    }




    public GarageResponseDTO getGarageFromService(ServiceResponseDTO service) {
        Garage garage = garageRepository.findById(service.getGarageId())
                .orElseThrow(() -> new GarageExceptions.ResourceNotFoundException("Garage not found for service"));

        GarageResponseDTO dto = new GarageResponseDTO();
        dto.setGarageId(garage.getId());
        dto.setBusinessName(garage.getBusinessName());
        dto.setMpesaTill(garage.getMpesaTill());
        return dto;
    }
}
