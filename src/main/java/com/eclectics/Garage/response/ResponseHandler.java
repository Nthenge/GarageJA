package com.eclectics.Garage.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ResponseHandler {

    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object data, String path) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", status.is2xxSuccessful()); //will return true or false
        response.put("message", message);
        response.put("path", path);
        response.put("timestamp", LocalDateTime.now());
        response.put("data", data);


        return new ResponseEntity<>(response, status);
    }
}

