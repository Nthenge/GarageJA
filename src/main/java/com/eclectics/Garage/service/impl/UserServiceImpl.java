package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.*;
import com.eclectics.Garage.mapper.UserMapper;
import com.eclectics.Garage.model.Role;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.CarOwnerRepository;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.security.TokenEncryptor;
import com.eclectics.Garage.service.UserService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.exception.GarageExceptions.UnauthorizedException;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ForbiddenException;

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
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UsersRepository usersRepository;
    private final JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final CarOwnerRepository carOwnerRepository;
    private final GarageRepository garageRepository;
    private final MechanicRepository mechanicRepository;

    public UserServiceImpl(UsersRepository usersRepository, JavaMailSender javaMailSender, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserMapper mapper, CarOwnerRepository carOwnerRepository, GarageRepository garageRepository, MechanicRepository mechanicRepository) {
        this.usersRepository = usersRepository;
        this.javaMailSender = javaMailSender;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.carOwnerRepository = carOwnerRepository;
        this.garageRepository = garageRepository;
        this.mechanicRepository = mechanicRepository;
    }

    private String decryptToken(String token) {
        try {
            return TokenEncryptor.decrypt(token);
        } catch (Exception e) {
            logger.error("Token decryption failed: {}", e.getMessage());
            throw new ForbiddenException("Invalid token format");
        }
    }

    private static SimpleMailMessage getMailMessage(String email, String token, String subject, String urlPath) {
        String fullUrl = "http://10.20.33.74:4200" + urlPath + "/token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText("Hello,\n\n" +
                "Please click the link below:\n" +
                fullUrl + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\nGarage Team");
        return message;
    }

    @Override
    public UserRegistrationResponseDTO createUser(UserRegistrationRequestDTO requestDTO) {
        logger.info("Creating new user with email: {}", requestDTO.getEmail());

        if (getUserByEmail(requestDTO.getEmail()).isPresent()) {
            logger.warn("Attempted to create user with existing email: {}", requestDTO.getEmail());
            throw new BadRequestException("Email is already in registered.");
        }

        User user = mapper.toEntity(requestDTO);
        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = usersRepository.save(user);
        logger.debug("User saved successfully with ID: {}", savedUser.getId());

        String token = jwtUtil.generateEmailConfirmToken(savedUser.getEmail(), savedUser.getRole().name());
        confirmEmail(savedUser.getEmail(), token);

        logger.info("Confirmation email sent to: {}", savedUser.getEmail());
        return mapper.toResponseDTO(savedUser);
    }

    @Override
    public void confirmEmail(String email, String token) {
        logger.info("Preparing confirmation email for user: {}", email);

        SimpleMailMessage message = getMailMessage(
                email,
                token,
                "Confirm your Garage Account",
                "/user/confirm"
        );

        try {
            javaMailSender.send(message);
            logger.info("Confirmation email successfully sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send confirmation email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public boolean confirmUser(String rawToken) {
        logger.info("Confirming user with raw token.");

        String token = decryptToken(rawToken);

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
            logger.error("Error confirming user with token {}: {}", rawToken, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsAuthDTO loginUser(UserLoginRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String password = requestDTO.getPassword();

        logger.info("Attempting login for email: {}", email);

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found for email: {}", email);
                    return new BadRequestException("Login failed, email not found");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Invalid password attempt for user: {}", email);
            throw new BadRequestException("Login failed, Invalid password");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Please verify your email before logging in.");
        }

        logger.info("Login successful for user: {}", email);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        UserDetailsAuthDTO responseDTO = mapper.toAuthDetailsResponseDTO(user);
        responseDTO.setToken(token);
        boolean detailsCompleted = user.isDetailsCompleted();
        responseDTO.setDetailsCompleted(detailsCompleted);

        if (!detailsCompleted) {
            switch (user.getRole()) {
                case SYSTEM_ADMIN:
                    responseDTO.setDetailsCompleted(true);
                    break;

                case CAR_OWNER:
                    carOwnerRepository.findByUser(user).ifPresent(carOwner ->
                            responseDTO.setMissingFields(carOwner.getMissingFields())
                    );
                    break;

                case GARAGE_ADMIN:
                    garageRepository.findByUser(user).ifPresent(garage ->
                            responseDTO.setMissingFields(garage.getMissingFields())
                    );
                    break;

                case MECHANIC:
                    mechanicRepository.findByUser(user).ifPresent(mechanic ->
                            responseDTO.setMissingFields(mechanic.getMissingFields())
                    );
                    break;

                default:
                    responseDTO.setMissingFields(List.of("Unknown role – cannot determine missing fields"));
            }
        }

        return responseDTO;
    }

    @Override
    public UserPasswordResetResponseDTO resetPassword(UserPasswordResetRequestDTO resetRequestDTO) {
        String email = resetRequestDTO.getEmail();
        logger.info("Reset password request for email: {}", email);

        usersRepository.findByEmail(email).ifPresent(user -> {

            String resetToken = jwtUtil.generateResetPasswordToken(user.getEmail());

            sendResetEmail(user.getEmail(), resetToken);

            logger.info("Password reset flow successfully initiated for user: {}", email);
        });
        return new UserPasswordResetResponseDTO(
                "If an account with that email address exists, a password reset link has been sent."
        );
    }

    @Override
    public void sendResetEmail(String email, String token) {
        logger.info("Sending password reset email to: {}", email);

        SimpleMailMessage message = getMailMessage(
                email,
                token,
                "Reset Your Password",
                "/reset-password"
        );

        try {
            javaMailSender.send(message);
            logger.info("Password reset email successfully sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send reset email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void updatePassword(UserPasswordUpdateDTO updateDTO) {
        String token = updateDTO.getToken();
        String newPassword = updateDTO.getNewPassword();

        logger.info("Attempting password update using token.");

        String decryptedToken = decryptToken(token);
        String email = jwtUtil.extractEmailFromToken(decryptedToken);

        if (email == null || !jwtUtil.validateResetPasswordToken(decryptedToken, email)) {
            logger.warn("Invalid or expired reset token for password update");
            throw new UnauthorizedException("Invalid or expired reset token");
        }

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Password update failed: User not found despite valid token signature.");
                    return new UnauthorizedException("Invalid or expired token.");
                });

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        logger.info("Password successfully updated for user: {}", email);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public Optional<UserRegistrationResponseDTO> getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        return mapper.toOptionalResponse(usersRepository.findByEmail(email));
    }

    @Override
    @Cacheable(value = "allUsers")
    public List<UserRegistrationResponseDTO> getAllUsers() {
        logger.info("Fetching all users");
        return mapper.toResponseList(usersRepository.findAll());
    }

    @Override
    @CachePut(value = "users", key = "#user.email")
    public UserRegistrationResponseDTO updateUser(Long id, User user) {
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
            return mapper.toResponseDTO(updatedUser);
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
