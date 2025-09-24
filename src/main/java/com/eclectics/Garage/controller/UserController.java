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


    //updateUser
    @PutMapping("/{userId}")
    public  ResponseEntity<?>updateUser(@PathVariable Long userId, @RequestBody User user){
        userService.updateUser(userId, user);
        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }

    //deleteUser
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }
}
