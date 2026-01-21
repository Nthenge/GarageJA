package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.*;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.MechanicService;
import com.eclectics.Garage.service.UserService;

import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ForbiddenException;

import jakarta.validation.Valid;
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
    private final MechanicService mechanicService;

    public UserController(UserService userService, MechanicService mechanicService) {
        this.userService = userService;
        this.mechanicService = mechanicService;
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<Object> verifyResetToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        boolean valid = userService.validateResetToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);

        if(valid){
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Token invalid or expired");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','MECHANIC')")
    @GetMapping("/search")
    public ResponseEntity<Object> filterUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String secondname
    ) {
        List<UserRegistrationResponseDTO> users = userService.filterUsers(email, firstname, secondname);

        return ResponseHandler.generateResponse(
                "Filtered users retrieved successfully",
                HttpStatus.OK,
                users,
                "/user/search"
        );
    }

    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRegistrationRequestDTO user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        try {
            userService.createUser(user);
            return ResponseHandler.generateResponse("To finish registration, Please confirm your email", HttpStatus.CREATED, null, "/user/register");

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("garage/register/mechanic")
    public ResponseEntity<?> createMechanic(@Valid @RequestBody MechanicGarageRegisterRequestDTO mechUser){
            User dto = mechanicService.registerMechanic(mechUser);
            return ResponseHandler.generateResponse("Mechanic created", HttpStatus.CREATED, dto, "/user/garage/register/mechanic");
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }
        UserDetailsAuthDTO responseDTO = (UserDetailsAuthDTO) userService.loginUser(requestDTO);
        return ResponseHandler.generateResponse("Login success", HttpStatus.OK, responseDTO, "/user/login");
    }

    @PostMapping("/confirm")
    public ResponseEntity<Object> confirmAccount(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");

        if (token == null) {
            throw new BadRequestException("Token is required");
        }

        boolean confirmed = userService.confirmUser(token);

        if (confirmed) {
            return ResponseHandler.generateResponse("Account confirmed successfully!", HttpStatus.CREATED, null, "/user/confirm");
        } else {
            throw new ForbiddenException("Invalid or expired token");
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<Object> confirmAccountFromLink(@RequestParam("token") String token) {

        boolean confirmed = userService.confirmUser(token);

        if (confirmed) {
            return ResponseHandler.generateResponse("Your account has been confirmed!", HttpStatus.CREATED, null, "/user/confirm");
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
        return ResponseHandler.generateResponse("Reset PassWord success", HttpStatus.OK, responseDTO, "/user/reset-password");
    }

    @PostMapping("/update-password")
    public ResponseEntity<Object> updatePassword(@RequestBody UserPasswordUpdateDTO updateDTO) {

        if (updateDTO.getToken() == null || updateDTO.getNewPassword() == null) {
            throw new BadRequestException("Token, and new password are required");
        }
        UserPasswordUpdateDTO updateUser = userService.updatePassword(updateDTO);

        return ResponseHandler.generateResponse("Password updated successfully", HttpStatus.CREATED, null, "/user/update-password");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<Object> updateOwnProfile(@RequestBody User user) {
        UserRegistrationResponseDTO userUpdate = userService.updateUser(user);
        return ResponseHandler.generateResponse("Profile updated successfully", HttpStatus.CREATED, userUpdate, "/user/update");
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete-account")
    public ResponseEntity<Object> deleteOwnAccount() {
        userService.deletePersonalAccount();
        Map<String, String> response = new HashMap<>();
        return ResponseHandler.generateResponse("Your account has been deleted successfully.",HttpStatus.OK, response, "/user/delete-account");
    }


    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
        return ResponseHandler.generateResponse("User deleted", HttpStatus.OK, null, "/user/delete/{userId}");
    }
}