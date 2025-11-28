package com.eclectics.Garage.controller;

import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService   userService) {
        this.userService = userService;
    }

    @PutMapping("/suspend/{userId}")
    public ResponseEntity<Object> suspendUser(@PathVariable Long userId) {
        userService.suspendUser(userId);
        Map<String, String> response = new HashMap<>();
        return ResponseHandler.generateResponse("User suspended successfully", HttpStatus.OK, response, "/admin/suspend/{userId}");
    }

    @PutMapping("/unsuspend/{userId}")
    public ResponseEntity<Object> unsuspendUser(@PathVariable Long userId) {
        userService.unsuspendUser(userId);
        Map<String, String> response = new HashMap<>();
        return ResponseHandler.generateResponse("User unsuspended successfully", HttpStatus.OK, response, "/admin/unsuspend/{userId}");
    }
}

