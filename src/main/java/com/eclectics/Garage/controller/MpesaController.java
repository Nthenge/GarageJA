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

    public MpesaController(MpesaStkPushService mpesaStkPushService,
                           MpesaCallbackService mpesaCallbackService) {
        this.mpesaStkPushService = mpesaStkPushService;
        this.mpesaCallbackService = mpesaCallbackService;
    }

    @PostMapping("/stkpush")
    public ResponseEntity<String> stkPush(@RequestParam String phone, @RequestParam String amount)
            throws Exception {
        String response = mpesaStkPushService.initiateStkPush(phone, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody MpesaCallbackDTO callback) {
        try {
            mpesaCallbackService.handleCallback(callback);
            return ResponseEntity.ok("{ \"ResponseCode\":\"0\", \"ResponseDesc\":\"Callback received successfully\" }");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("{ \"ResponseCode\":\"0\", \"ResponseDesc\":\"Callback received, but processing failed internally\" }");
        }
    }
}