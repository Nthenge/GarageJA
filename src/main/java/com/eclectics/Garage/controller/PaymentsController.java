package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.Payment;
import com.eclectics.Garage.model.PaymentCurrency;
import com.eclectics.Garage.model.PaymentMethod;
import com.eclectics.Garage.model.PaymentStatus;
import com.eclectics.Garage.security.CustomUserDetails;
import com.eclectics.Garage.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentsController {

    final PaymentService paymentService;

    public PaymentsController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/initiate")
    public Payment initiatePayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long serviceId,
            @RequestParam(required = false)PaymentCurrency paymentCurrency,
            @RequestParam(required = false)PaymentMethod paymentMethod
            ){
        Payment payment = paymentService.initiatePayment(customUserDetails.getUsername(), serviceId);
        return payment;
    }

    @GetMapping("/payment/{paymentId}")
    public Optional<Payment> paymentByPaymentId(@PathVariable Integer paymentId){
        return paymentService.getPaymentByPaymentId(paymentId);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','CAR_OWNER')")
    @GetMapping("/owner/{ownerId}")
    public List<Payment> ownerPayments(@PathVariable Integer ownerId){
        return paymentService.getAllPaymentsDoneByOwner(ownerId);
    }

    @GetMapping("/service/{ownerId}")
    public List<Payment> allServicePayments(@PathVariable Long serviceId){
        return paymentService.getAllPaymentsByService(serviceId);
    }

    @PreAuthorize("hasRole('CAR_OWNER')")
    @PutMapping("/update/{paymentId}")
    public Payment updateAPayment(
            @PathVariable Integer paymentId,
            @RequestParam(required = true) PaymentStatus paymentStatus,
            @RequestParam(required = false) String transactionRef,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) PaymentCurrency paymentCurrency) {

        return paymentService.updatePayment(paymentId, paymentStatus, transactionRef, paymentMethod, paymentCurrency);
    }


    @PreAuthorize("hasRole('CAR_OWNER')")
    @DeleteMapping("/delete/{paymentId}")
    public String deletePayment(@PathVariable Integer paymentId){
        return paymentService.deletePayment(paymentId);
    }
}
