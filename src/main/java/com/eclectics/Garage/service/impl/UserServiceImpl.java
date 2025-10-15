package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.Role;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.service.UserService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.exception.GarageExceptions.UnauthorizedException;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UsersRepository usersRepository;
    private final JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UsersRepository usersRepository, JavaMailSender javaMailSender, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.javaMailSender = javaMailSender;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        logger.info("Creating new user with email: {}", user.getEmail());

        if (getUserByEmail(user.getEmail()).isPresent()) {
            logger.warn("Attempted to create user with existing email: {}", user.getEmail());
            throw new BadRequestException("Email is already in use");
        }

        user.setEnabled(true); // to set this to "false" for production
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = usersRepository.save(user);
        logger.debug("User saved successfully with ID: {}", savedUser.getId());

        String token = jwtUtil.generateEmailConfirmToken(savedUser.getEmail(), savedUser.getRole().name());
        confirmEmail(savedUser.getEmail(), token);

        logger.info("Confirmation email sent to: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    public void confirmEmail(String email, String token) {
        logger.info("Preparing confirmation email for user: {}", email);

        String confirmationLink = "http://192.168.1.65:8083/users/confirm?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirm your Garage Account");
        message.setText("Hello,\n\n" +
                "Welcome to Garage App! Please confirm your registration by clicking the link below:\n" +
                confirmationLink + "\n\n" +
                "If you did not register, please ignore this email.\n\n" +
                "Regards,\nGarage Team");

        try {
            javaMailSender.send(message);
            logger.info("Confirmation email successfully sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send confirmation email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public boolean confirmUser(String token) {
        logger.info("Confirming user with token: {}", token);
        try {
            String email = jwtUtil.extractEmail(token);
            Optional<User> optionalUser = usersRepository.findByEmail(email);

            if (optionalUser.isEmpty()) {
                logger.warn("User not found for email extracted from token: {}", email);
                return false;
            }

            User user = optionalUser.get();

            if (user.isEnabled()) {
                logger.info("User {} already confirmed", user.getEmail());
                return true;
            }

            user.setEnabled(true);
            usersRepository.save(user);
            logger.info("User {} confirmed successfully", user.getEmail());
            return true;
        } catch (Exception e) {
            logger.error("Error confirming user with token {}: {}", token, e.getMessage());
            return false;
        }
    }

    @Override
    public User loginUser(String email, String password) {
        logger.info("Attempting login for email: {}", email);

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found for email: {}", email);
                    return new BadRequestException("User/Email does not exist, Check your email or register");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Invalid password attempt for user: {}", email);
            throw new BadRequestException("Invalid password");
        }

        logger.info("Login successful for user: {}", email);
        return user;
    }

    @Override
    public User resetPassword(String email) {
        logger.info("Reset password request for email: {}", email);
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Password reset failed - user not found for email: {}", email);
                    return new ResourceNotFoundException("Email does not exist");
                });
    }

    @Override
    public void sendResetEmail(String email, String token) {
        logger.info("Sending password reset email to: {}", email);

        String resetUrl = "http://192.168.1.65:8083/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Your Password");
        message.setText("Hello,\n\n" +
                "You requested to reset your password. Please click the link below:\n" +
                resetUrl + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\nGarage Team");

        try {
            javaMailSender.send(message);
            logger.info("Password reset email successfully sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send reset email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public User updatePassword(String token, String newPassword) {
        logger.info("Updating password for token: {}", token);

        String email = jwtUtil.extractEmailFromToken(token);
        if (email == null || !jwtUtil.validateResetPasswordToken(token, email)) {
            logger.warn("Invalid or expired reset token for password update");
            throw new UnauthorizedException("Invalid or expired reset token");
        }

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        logger.info("Password successfully updated for user: {}", email);
        return user;
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public Optional<User> getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        return usersRepository.findByEmail(email);
    }

    @Override
    @Cacheable(value = "allUsers")
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return usersRepository.findAll();
    }

    @Override
    @CachePut(value = "users", key = "#user.email")
    public User updateUser(Long id, User user) {
        logger.info("Updating user with ID: {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        logger.debug("Authenticated user performing update: {}", currentUserEmail);

        User currentUser = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        return usersRepository.findById(id).map(existingUser -> {
            logger.debug("Found existing user with ID: {}", id);

            if (user.getRole() != null && !user.getRole().equals(existingUser.getRole())) {
                if (!Role.SYSTEM_ADMIN.equals(currentUser.getRole())) {
                    logger.warn("Unauthorized role change attempt by user: {}", currentUserEmail);
                    throw new UnauthorizedException("You are not authorized to change roles");
                }
                logger.info("User {} updated role to {}", existingUser.getEmail(), user.getRole());
                existingUser.setRole(user.getRole());
            }

            if (user.getSecondname() != null) existingUser.setSecondname(user.getSecondname());
            if (user.getFirstname() != null) existingUser.setFirstname(user.getFirstname());
            if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
            if (user.getPhoneNumber() != null) existingUser.setPhoneNumber(user.getPhoneNumber());

            User updatedUser = usersRepository.save(existingUser);
            logger.info("User {} updated successfully", updatedUser.getEmail());
            return updatedUser;
        }).orElseThrow(() -> {
            logger.error("User with ID {} not found for update", id);
            return new ResourceNotFoundException("User with id " + id + " not found.");
        });
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        if (!usersRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent user with ID: {}", id);
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }

        usersRepository.deleteById(id);
        logger.info("User with ID {} deleted successfully", id);
    }
}
