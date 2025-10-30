package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.*;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.service.UserService;

import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ForbiddenException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private ResponseEntity<Map<String, String>> success(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }

    //    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN,'GARAGE_ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<?> getOneUser(@PathVariable String email ){
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @GetMapping()
    public ResponseEntity<List<UserRegistrationResponseDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationRequestDTO user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        try {
            userService.createUser(user);
            return ResponseEntity.ok(Map.of(
                    "message", "To finish registration, Please confirm your email"
            ));

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDetailsAuthDTO> login(@RequestBody UserLoginRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }
        UserDetailsAuthDTO responseDTO = (UserDetailsAuthDTO) userService.loginUser(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmAccount(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");

        if (token == null) {
            throw new BadRequestException("Token is required");
        }

        boolean confirmed = userService.confirmUser(token);

        if (confirmed) {
            return success("Account confirmed successfully!");
        } else {
            throw new ForbiddenException("Invalid or expired token");
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmAccountFromLink(@RequestParam("token") String token) {

        boolean confirmed = userService.confirmUser(token);

        if (confirmed) {
            return success("Your account has been confirmed!");
        } else {
            throw new ForbiddenException("Invalid or expired token");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<UserPasswordResetResponseDTO> requestResetPassword(
            @RequestBody UserPasswordResetRequestDTO requestDTO) {

        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        UserPasswordResetResponseDTO responseDTO = userService.resetPassword(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UserPasswordUpdateDTO updateDTO) {

        if (updateDTO.getToken() == null || updateDTO.getNewPassword() == null) {
            throw new BadRequestException("Token, and new password are required");
        }
        userService.updatePassword(updateDTO);

        return success("Password updated successfully");
    }

    @PutMapping("/update/{userId}")
    public  ResponseEntity<?>updateUser(@PathVariable Long userId, @RequestBody User user){
        userService.updateUser(userId, user);
        return success("User updated successfully");
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
        return success("User deleted");
    }
}