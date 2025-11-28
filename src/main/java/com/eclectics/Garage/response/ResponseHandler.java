package com.eclectics.Garage.response;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ResponseHandler {

    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", status.is2xxSuccessful());
        response.put("message", message);
        response.put("data", data);

        return new ResponseEntity<>(response, status);
    }
}

