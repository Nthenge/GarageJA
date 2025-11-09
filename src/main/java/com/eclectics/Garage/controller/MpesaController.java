package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.MpesaCallbackDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.service.MpesaCallbackService;
import com.eclectics.Garage.service.MpesaStkPushService;
import com.eclectics.Garage.service.PaymentSplitService;
import com.eclectics.Garage.service.MpesaB2CService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mpesa")
public class MpesaController {

    private final MpesaStkPushService mpesaStkPushService;
    private final MpesaCallbackService mpesaCallbackService;
    private final PaymentSplitService paymentSplitService;
    private final MpesaB2CService mpesaB2CService;

    public MpesaController(MpesaStkPushService mpesaStkPushService,
                           MpesaCallbackService mpesaCallbackService,
                           PaymentSplitService paymentSplitService,
                           MpesaB2CService mpesaB2CService) {
        this.mpesaStkPushService = mpesaStkPushService;
        this.mpesaCallbackService = mpesaCallbackService;
        this.paymentSplitService = paymentSplitService;
        this.mpesaB2CService = mpesaB2CService;
    }

    // === STK Push Request ===
    @PostMapping("/stkpush")
    public ResponseEntity<String> stkPush(@RequestParam String phone, @RequestParam String amount)
            throws Exception {
        String response = mpesaStkPushService.initiateStkPush(phone, amount);
        return ResponseEntity.ok(response);
    }

    // === Callback ===
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody MpesaCallbackDTO callback) {
        // Step 1: Confirm payment success
        if (callback.getBody().getStkCallback().getResultCode() == 0) {

            // Step 2: Identify service and garage (from database)
            ServiceResponseDTO service = mpesaCallbackService.getServiceFromCallback(callback);
            GarageResponseDTO garage = mpesaCallbackService.getGarageFromService(service);

            // Step 3: Calculate payment split
            PaymentSplitService.PaymentSplitResult split =
                    paymentSplitService.calculateSplit(service, garage);

            // Step 4: Send garage’s 95% share
            try {
                mpesaB2CService.sendMoneyToGarage(
                        split.getGarageAmount(),
                        split.getGarageTill().toString(),
                        "Garage payment for service ID: " + service.getId()
                );
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Error sending money to garage");
            }
        }

        return ResponseEntity.ok("Callback processed successfully");
    }
}
