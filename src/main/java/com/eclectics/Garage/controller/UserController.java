package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.*;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.UserService;

import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ForbiddenException;

import jakarta.validation.Valid;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOuterJoinEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN,'GARAGE_ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<Object> getOneUser(@PathVariable String email ){
        UserRegistrationResponseDTO user = userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()).getBody();
        return ResponseHandler.generateResponse("User by email", HttpStatus.OK, user);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @GetMapping()
    public ResponseEntity<Object> getAllUsers(){
        List<UserRegistrationResponseDTO> list = userService.getAllUsers();
        return ResponseHandler.generateResponse("List of all Users", HttpStatus.OK, list);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRegistrationRequestDTO user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        try {
            userService.createUser(user);
            return ResponseHandler.generateResponse("To finish registration, Please confirm your email", HttpStatus.CREATED, null);

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
    public ResponseEntity<Object> confirmAccount(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");

        if (token == null) {
            throw new BadRequestException("Token is required");
        }

        boolean confirmed = userService.confirmUser(token);

        if (confirmed) {
            return ResponseHandler.generateResponse("Account confirmed successfully!", HttpStatus.CREATED, null);
        } else {
            throw new ForbiddenException("Invalid or expired token");
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<Object> confirmAccountFromLink(@RequestParam("token") String token) {

        boolean confirmed = userService.confirmUser(token);

        if (confirmed) {
            return ResponseHandler.generateResponse("Your account has been confirmed!", HttpStatus.CREATED, null);
        } else {
            throw new ForbiddenException("Invalid or expired token");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> requestResetPassword(
            @RequestBody UserPasswordResetRequestDTO requestDTO) {

        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        UserPasswordResetResponseDTO responseDTO = userService.resetPassword(requestDTO);
        return ResponseHandler.generateResponse("Reset PassWord success", HttpStatus.OK, responseDTO);
    }

    @PostMapping("/update-password")
    public ResponseEntity<Object> updatePassword(@RequestBody UserPasswordUpdateDTO updateDTO) {

        if (updateDTO.getToken() == null || updateDTO.getNewPassword() == null) {
            throw new BadRequestException("Token, and new password are required");
        }
        UserPasswordUpdateDTO updateUser = userService.updatePassword(updateDTO);

        return ResponseHandler.generateResponse("Password updated successfully", HttpStatus.CREATED, updateUser);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<Object> updateOwnProfile(@RequestBody User user) {
        UserRegistrationResponseDTO userUpdate = userService.updateUser(user);
        return ResponseHandler.generateResponse("Profile updated successfully", HttpStatus.CREATED, userUpdate);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete-account")
    public ResponseEntity<Object> deleteOwnAccount() {
        userService.deletePersonalAccount();
        Map<String, String> response = new HashMap<>();
        return ResponseHandler.generateResponse("Your account has been deleted successfully.",HttpStatus.OK, response);
    }


    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
        return ResponseHandler.generateResponse("User deleted", HttpStatus.OK, null);
    }
}