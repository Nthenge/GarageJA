package com.eclectics.Garage.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GarageExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GarageExceptionHandler.class);

    private ResponseEntity<Object> buildResponseEntity(Exception ex, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("time", new Date());
        response.put("status", status.value());
        response.put("error", ex.getClass().getSimpleName());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GarageExceptions.ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(GarageExceptions.ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GarageExceptions.BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(GarageExceptions.BadRequestException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GarageExceptions.UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(GarageExceptions.UnauthorizedException ex) {
        logger.warn("Unauthorized access attempt: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(GarageExceptions.ForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(GarageExceptions.ForbiddenException ex) {
        logger.warn("Forbidden action: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.FORBIDDEN);
    }
}
