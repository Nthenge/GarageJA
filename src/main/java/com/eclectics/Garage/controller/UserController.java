package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //Get one user
    @GetMapping("/{email}")
    public ResponseEntity<?> getOneUser(@PathVariable String email ){
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //Get all users
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //PostUser
    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody User user){
        try {
            // This method now throws RuntimeException if email already exists
            User savedUser = userService.createUser(user);

            // Successful registration response
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "message", "User registered successfully",
                            "userId", savedUser.getId().toString()
                    ));

        } catch (RuntimeException e) {
            // Friendly error response (like "Email already registered")
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Catch other unexpected errors
            return ResponseEntity
                    .status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String password = payload.get("password");

            User user = userService.loginUser(email, password);

            // generate JWT token
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "email", user.getEmail(),
                    "role", user.getRole().name(),
                    "firstname", user.getFirstname()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", e.getMessage()));
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
