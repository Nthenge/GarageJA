package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.CarOwnerRepository;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.security.TokenEncryptor;
import com.eclectics.Garage.service.UserService;

import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ForbiddenException;
import com.eclectics.Garage.exception.GarageExceptions.UnauthorizedException;

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
    private final JwtUtil jwtUtil;
    private final CarOwnerRepository carOwnerRepository;
    private final GarageRepository garageRepository;
    private final MechanicRepository mechanicRepository;

    public UserController(UserService userService, JwtUtil jwtUtil, CarOwnerRepository carOwnerRepository, GarageRepository garageRepository, MechanicRepository mechanicRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.carOwnerRepository = carOwnerRepository;
        this.garageRepository = garageRepository;
        this.mechanicRepository = mechanicRepository;
    }

    private ResponseEntity<Map<String, String>> success(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN,'GARAGE_ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<?> getOneUser(@PathVariable String email ){
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER','MECHANIC')")
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        try {
            userService.createUser(user);
            return ResponseEntity.ok(Map.of(
                    "message", "To finish registration, Please confirm your email"
            ));

        } catch (BadRequestException e) {
            if ("Email is already in use".equals(e.getMessage())) {
                throw new BadRequestException("Email is already in use");
            }
            throw new BadRequestException("Unable to register user: " + e.getMessage());
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER','MECHANIC')")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || password == null) {
            throw new BadRequestException("Email and password are required");
        }

        User user = userService.loginUser(email, password);

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Please verify your email before logging in.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().name());
        response.put("firstname", user.getFirstname());
        response.put("detailsCompleted", user.isDetailsCompleted());

        if (!user.isDetailsCompleted()) {
            switch (user.getRole().name()) {
                case "SYSTEM_ADMIN":
                    response.put("detailsCompleted", true);
                    break;

                case "CAR_OWNER":
                    carOwnerRepository.findByUser(user).ifPresent(carOwner ->
                            response.put("missingFields", carOwner.getMissingFields())
                    );
                    break;

                case "GARAGE_ADMIN":
                    garageRepository.findByUser(user).ifPresent(garage ->
                            response.put("missingFields", garage.getMissingFields())
                    );
                    break;

                case "MECHANIC":
                    mechanicRepository.findByUser(user).ifPresent(mechanic ->
                            response.put("missingFields", mechanic.getMissingFields())
                    );
                    break;

                default:
                    response.put("missingFields", List.of("Unknown role – cannot determine missing fields"));
            }
        }
        return ResponseEntity.ok(response);
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER','MECHANIC')")
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmAccount(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        boolean enabled = payload.get("enabled") != null && (boolean) payload.get("enabled");

        if (!enabled) {
            throw new UnauthorizedException("Account not enabled");
        }

        String decryptedToken;
        try {
            decryptedToken = TokenEncryptor.decrypt(token);
        } catch (Exception e) {
            throw new ForbiddenException("Invalid token format");
        }

        boolean confirmed = userService.confirmUser(decryptedToken);
        if (confirmed) {
            return success("Account confirmed successfully!");
        } else {
            throw new ForbiddenException("Invalid or expired token");
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER','MECHANIC')")
    @GetMapping("/confirm")
    public ResponseEntity<?> confirmAccountFromLink(@RequestParam("token") String token) {

        String decryptedToken;
        try {
            decryptedToken = TokenEncryptor.decrypt(token);
        } catch (Exception e) {
            throw new ForbiddenException("Invalid token format");
        }

        boolean confirmed = userService.confirmUser(decryptedToken);
        if (confirmed) {
            return success("Your account has been confirmed!");
        } else {
            throw new ForbiddenException("Invalid or expired token");
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER','MECHANIC')")
    @PostMapping("/reset-password")
    public ResponseEntity<?> requestResetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        User user = userService.resetPassword(email);
        String resetToken = jwtUtil.generateResetPasswordToken(user.getEmail());
        userService.sendResetEmail(user.getEmail(), resetToken);

        return success("Password reset link sent to " + email);
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER','MECHANIC')")
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        if (token == null || newPassword == null) {
            throw new BadRequestException("Token and new password are required");
        }

        userService.updatePassword(token, newPassword);

        return success("Password updated successfully");
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER','MECHANIC')")
    @PutMapping("/update/{userId}")
    public  ResponseEntity<?>updateUser(@PathVariable Long userId, @RequestBody User user){
        userService.updateUser(userId, user);
        return success("User updated successfully");
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER','MECHANIC')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
        return success("User deleted");
    }
}
