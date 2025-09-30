package com.eclectics.Garage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mpesa")
public class MpesaController {

    @PostMapping("/transaction")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> payload) {
        System.out.println("M-Pesa Callback: " + payload);
        return ResponseEntity.ok("Callback received");
    }
}

