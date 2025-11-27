package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.*;
import com.eclectics.Garage.mapper.UserMapper;
import com.eclectics.Garage.model.Role;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.CustomUserDetails;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.security.TokenEncryptor;
import com.eclectics.Garage.service.UserService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.exception.GarageExceptions.UnauthorizedException;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ForbiddenException;

import com.eclectics.Garage.specificationExecutor.UserSpecificationExecutor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final Map<String, User> userCache = new ConcurrentHashMap<>();
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> resetTokenCache = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Queue<String> recentLogins = new LinkedList<>();

    private final UsersRepository usersRepository;
    private final JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserServiceImpl(UsersRepository usersRepository, JavaMailSender javaMailSender, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserMapper mapper) {
        this.usersRepository = usersRepository;
        this.javaMailSender = javaMailSender;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
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
            throw new BadRequestException("Email is already registered.");
        }

        User user = mapper.toEntity(requestDTO);
        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = usersRepository.save(user);
        userCache.put(savedUser.getEmail(), savedUser);

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
            userCache.put(user.getEmail(), user);

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

        int attempts = loginAttempts.getOrDefault(email, 0);
        if (attempts >= 5) {
            throw new UnauthorizedException("Too many failed attempts. Try again later.");
        }

        User user = userCache.getOrDefault(email,
                usersRepository.findByEmail(email)
                        .orElseThrow(() -> new BadRequestException("Login failed, email not found"))
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttempts.put(email, attempts + 1);
            logger.warn("Invalid password attempt for user: {}", email);
            throw new BadRequestException("Login failed, Invalid password");
        }

        loginAttempts.remove(email);

        if (recentLogins.size() >= 10) {
            recentLogins.poll();
        }
        recentLogins.offer(email);

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Please verify your email before logging in.");
        }

        if (user.isSuspended()) {
            throw new UnauthorizedException("Your account has been suspended. Contact system administrator.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        UserDetailsAuthDTO responseDTO = mapper.toAuthDetailsResponseDTO(user);
        responseDTO.setToken(token);
        responseDTO.setDetailsCompleted(user.isDetailsCompleted());

        logger.info("Login successful for user: {}", email);
        return responseDTO;
    }

    @Override
    public UserPasswordResetResponseDTO resetPassword(UserPasswordResetRequestDTO resetRequestDTO) {
        String email = resetRequestDTO.getEmail();
        logger.info("Reset password request for email: {}", email);

        usersRepository.findByEmail(email).ifPresent(user -> {
            String resetToken = jwtUtil.generateResetPasswordToken(user.getEmail());
            sendResetEmail(user.getEmail(), resetToken);
            resetTokenCache.put(resetToken, System.currentTimeMillis() + (15 * 60 * 1000));
            logger.info("Password reset token cached for {}", email);
        });

        return new UserPasswordResetResponseDTO(
                "If an account with that email exists, a reset link has been sent."
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
            logger.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send reset email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void suspendUser(Long userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.SYSTEM_ADMIN) {
            throw new UnauthorizedException("Cannot suspend another System Admin");
        }

        user.setSuspended(true);
        usersRepository.save(user);
        logger.info("User {} suspended successfully", user.getEmail());
    }

    @Override
    public void unsuspendUser(Long userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setSuspended(false);
        usersRepository.save(user);
        logger.info("User {} unsuspended successfully", user.getEmail());
    }

    @Override
    public UserPasswordUpdateDTO updatePassword(UserPasswordUpdateDTO updateDTO) {
        String token = updateDTO.getToken();
        String newPassword = updateDTO.getNewPassword();

        logger.info("Attempting password update using token.");

        if (!resetTokenCache.containsKey(token) || System.currentTimeMillis() > resetTokenCache.get(token)) {
            throw new UnauthorizedException("Invalid or expired reset token");
        }
        resetTokenCache.remove(token);

        String decryptedToken = decryptToken(token);
        String email = jwtUtil.extractEmailFromToken(decryptedToken);

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired token."));

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);
        userCache.put(email, user);

        logger.info("Password successfully updated for user: {}", email);
        return updateDTO;
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public Optional<UserRegistrationResponseDTO> getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);

        if (userCache.containsKey(email)) {
            logger.debug("Cache hit for user email: {}", email);
            return Optional.of(mapper.toResponseDTO(userCache.get(email)));
        }

        Optional<User> userOpt = usersRepository.findByEmail(email);
        userOpt.ifPresent(user -> userCache.put(email, user));
        return mapper.toOptionalResponse(userOpt);
    }

    @Cacheable(value = "allUsers")
    @Transactional(readOnly = true)
    @Override
    public List<UserRegistrationResponseDTO> filterUsers(
            String email,
            String firstname,
            String secondname
    ) {
        Specification<User> spec = Specification.allOf(
                UserSpecificationExecutor.emailContains(email),
                UserSpecificationExecutor.firstnameContains(firstname),
                UserSpecificationExecutor.secondnameContains(secondname)
        );

        List<User> users = usersRepository.findAll(spec);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users match the given criteria.");
        }

        return users.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }


    @Override
    @CachePut(value = "users", key = "#result.email")
    public UserRegistrationResponseDTO updateUser(User user) {
        logger.info("Updating profile for the logged-in user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        logger.debug("Authenticated user performing update: {}", currentUserEmail);

        User existingUser = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        if (user.getFirstname() != null) existingUser.setFirstname(user.getFirstname());
        if (user.getSecondname() != null) existingUser.setSecondname(user.getSecondname());
        if (user.getPhoneNumber() != null) existingUser.setPhoneNumber(user.getPhoneNumber());
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            existingUser.setEmail(user.getEmail());
        }

        User updatedUser = usersRepository.save(existingUser);
        userCache.put(updatedUser.getEmail(), updatedUser);

        logger.info("User {} updated successfully", updatedUser.getEmail());
        return mapper.toResponseDTO(updatedUser);
    }



    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        if (!usersRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent user with ID: {}", id);
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }

        usersRepository.findById(id).ifPresent(user -> userCache.remove(user.getEmail()));
        usersRepository.deleteById(id);

        logger.info("User with ID {} deleted successfully", id);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deletePersonalAccount() {
        logger.info("[DELETE] Attempting to delete currently authenticated user account...");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            logger.error("[DELETE FAILED] No authenticated user found in context.");
            throw new UnauthorizedException("You are not authenticated.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();

        User existingUser = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("[DELETE FAILED] User with ID {} not found", userId);
                    return new ResourceNotFoundException("User not found");
                });
        usersRepository.delete(existingUser);

        logger.info("[DELETE SUCCESS] User account deleted successfully for email={}", existingUser.getEmail());
    }


    //monitoring
    public List<String> getRecentLogins() {
        return new ArrayList<>(recentLogins);
    }
}
