package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.CarOwnerRepository;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
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

    @GetMapping("/{email}")
    public ResponseEntity<?> getOneUser(@PathVariable String email ){
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "message", "User registered successfully",
                            "token", token,
                            "role", savedUser.getRole().name()
                    ));

        } catch (RuntimeException e) {
            if ("Email is already in use".equals(e.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", 409,
                                "error", "Conflict",
                                "message", e.getMessage()
                        ));
            }
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "status", 400,
                            "error", "Bad Request",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", 500,
                            "error", "Internal Server Error",
                            "message", "An unexpected error occurred"
                    ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> requestResetPassword(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");

            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            }

            User user = userService.resetPassword(email);
            String resetToken = jwtUtil.generateResetPasswordToken(user.getEmail());
            userService.sendResetEmail(user.getEmail(), resetToken);

            return ResponseEntity.ok(Map.of(
                    "message", "Password reset link sent to " + email
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> payload) {
        try {
            String token = payload.get("token");
            String newPassword = payload.get("newPassword");

            if (token == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Token and new password are required"));
            }

            User updatedUser = userService.updatePassword(token, newPassword);

            return ResponseEntity.ok(Map.of(
                    "message", "Password updated successfully",
                    "user", updatedUser.getEmail()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String password = payload.get("password");

            if (email == null || password == null) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "status", 400,
                                "error", "Bad Request",
                                "message", "Email and password are required"
                        ));
            }

            User user = userService.loginUser(email, password);

            if (!user.isEnabled()) {
                throw new RuntimeException("Please verify your email before logging in.");
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole().name());
            response.put("firstname", user.getFirstname());
            response.put("detailsCompleted", user.isDetailsCompleted());

            if (!user.isDetailsCompleted()) {
                switch (user.getRole().name()) {
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

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", 401,
                            "error", "Unauthorized access",
                            "message", e.getMessage())
                    );
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmAccount(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        boolean enabled = payload.get("enabled") != null && (boolean) payload.get("enabled");

        if (!enabled) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account not enabled"));
        }

        boolean confirmed = userService.confirmUser(token);
        if (confirmed) {
            return ResponseEntity.ok(Map.of("message", "Account confirmed successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid or expired token"));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmAccountFromLink(@RequestParam("token") String token) {
        boolean confirmed = userService.confirmUser(token);
        if (confirmed) {
            return ResponseEntity.ok(Map.of("message", "Your account has been confirmed!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid or expired token"));
        }
    }

    @PutMapping("/{userId}")
    public  ResponseEntity<?>updateUser(@PathVariable Long userId, @RequestBody User user){
        userService.updateUser(userId, user);
        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }
}
