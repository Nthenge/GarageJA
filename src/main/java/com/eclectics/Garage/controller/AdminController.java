package com.eclectics.Garage.controller;

import com.eclectics.Garage.service.UserService;
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
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService   userService) {
        this.userService = userService;
    }

    @PutMapping("/suspend/{userId}")
    public ResponseEntity<Map<String, String>> suspendUser(@PathVariable Long userId) {
        userService.suspendUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User suspended successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/unsuspend/{userId}")
    public ResponseEntity<Map<String, String>> unsuspendUser(@PathVariable Long userId) {
        userService.unsuspendUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User unsuspended successfully");
        return ResponseEntity.ok(response);
    }
}

