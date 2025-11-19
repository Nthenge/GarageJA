package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.PaymentsResponseDTO;
import com.eclectics.Garage.model.Payment;
import com.eclectics.Garage.model.PaymentCurrency;
import com.eclectics.Garage.model.PaymentMethod;
import com.eclectics.Garage.model.PaymentStatus;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.security.CustomUserDetails;
import com.eclectics.Garage.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<Object> allPayments(){
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseHandler.generateResponse("All payaments", HttpStatus.OK, payments);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @PostMapping(value = "/initiate")
    public ResponseEntity<Object> initiatePayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long serviceId,
            @RequestParam(required = false)PaymentCurrency paymentCurrency,
            @RequestParam(required = false)PaymentMethod paymentMethod
            ){
        PaymentsResponseDTO payment = paymentService.initiatePayment(customUserDetails.getUsername(), serviceId);
        return ResponseHandler.generateResponse("Payment initiated", HttpStatus.CREATED, payment);
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<Object> paymentByPaymentId(@PathVariable Integer paymentId){
        Optional<PaymentsResponseDTO> paymenyById = paymentService.getPaymentByPaymentId(paymentId);
        return ResponseHandler.generateResponse("Payment by Id", HttpStatus.OK, paymenyById);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','CAR_OWNER')")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Object> ownerPayments(@PathVariable Integer ownerId){
        List<PaymentsResponseDTO> paymentsByOwner = paymentService.getAllPaymentsDoneByOwner(ownerId);
        return ResponseHandler.generateResponse("Payments made by an owner", HttpStatus.OK, paymentsByOwner);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/service/{ownerId}")
    public ResponseEntity<Object> allServicePayments(@PathVariable Long serviceId){
        List<PaymentsResponseDTO> allServicePayments = paymentService.getAllPaymentsByService(serviceId);
        return ResponseHandler.generateResponse("All payments for a particular service", HttpStatus.OK, allServicePayments);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER')") //think of this authorization
    @PutMapping("/update/{paymentId}")
    public ResponseEntity<Object> updateAPayment(
            @PathVariable Integer paymentId,
            @RequestParam(required = true) PaymentStatus paymentStatus,
            @RequestParam(required = false) String transactionRef,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) PaymentCurrency paymentCurrency) {

        PaymentsResponseDTO paymentsResponseDTO = paymentService.updatePayment(paymentId, paymentStatus, transactionRef, paymentMethod, paymentCurrency);
        return ResponseHandler.generateResponse("Update payment", HttpStatus.CREATED, paymentsResponseDTO);
    }


    @PreAuthorize("hasRole('CAR_OWNER')")
    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity<Object> deletePayment(@PathVariable Integer paymentId){
        paymentService.deletePayment(paymentId);
        return ResponseHandler.generateResponse("Payament Deleted", HttpStatus.OK, null);
    }
}
