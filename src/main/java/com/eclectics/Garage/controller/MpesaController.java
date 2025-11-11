package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.MpesaCallbackDTO;
import com.eclectics.Garage.service.MpesaCallbackService;
import com.eclectics.Garage.service.MpesaStkPushService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mpesa")
public class MpesaController {

    private final MpesaStkPushService mpesaStkPushService;
    private final MpesaCallbackService mpesaCallbackService;
    // Removed unused dependencies: PaymentSplitService and MpesaB2BService

    public MpesaController(MpesaStkPushService mpesaStkPushService,
                           MpesaCallbackService mpesaCallbackService) {
        this.mpesaStkPushService = mpesaStkPushService;
        this.mpesaCallbackService = mpesaCallbackService;
    }

    // 1. Initial STK Push Request
    @PostMapping("/stkpush")
    public ResponseEntity<String> stkPush(@RequestParam String phone, @RequestParam String amount)
            throws Exception {
        // Simple delegation: Controller initiates the payment process
        String response = mpesaStkPushService.initiateStkPush(phone, amount);
        return ResponseEntity.ok(response);
    }

    // 2. Safaricom's Response (Callback)
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody MpesaCallbackDTO callback) {

        // Simple delegation: Controller hands off the entire response body
        // to the service layer for processing, splitting, and saving.
        try {
            mpesaCallbackService.handleCallback(callback);

            // Safaricom requires a 200 OK response with a specific body
            // for successful callback receipt.
            return ResponseEntity.ok("{ \"ResponseCode\":\"0\", \"ResponseDesc\":\"Callback received successfully\" }");

        } catch (Exception e) {
            e.printStackTrace();
            // Important: Log the failure, but return success to avoid continuous retries from Daraja
            return ResponseEntity.ok("{ \"ResponseCode\":\"0\", \"ResponseDesc\":\"Callback received, but processing failed internally\" }");
        }
    }
}