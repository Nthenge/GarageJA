package com.eclectics.Garage.exception;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GarageExceptionHandler {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(GarageExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllEcxeption
            (Exception ex){

        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        Map<String, Object> garageException = new HashMap<>();
        garageException.put("Time", new Date());
        garageException.put("Status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        garageException.put("Error", ex.getClass().getSimpleName());
        garageException.put("Message", ex.getMessage());

        return new ResponseEntity<>(garageException, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
