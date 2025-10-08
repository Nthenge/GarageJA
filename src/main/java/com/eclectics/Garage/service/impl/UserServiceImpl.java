package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.service.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UsersRepository usersRepository, JavaMailSender javaMailSender, JwtUtil jwtUtil) {
        this.usersRepository = usersRepository;
        this.javaMailSender = javaMailSender;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User createUser(User user) {
        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }
        user.setEnabled(true); //set to false for production
        User savedUser = usersRepository.save(user);
        String token = jwtUtil.generateEmailConfirmToken(savedUser.getEmail(), savedUser.getRole().name());
        confirmEmail(savedUser.getEmail(), token);

        return savedUser;
    }

    @Override
    public void confirmEmail(String email, String token) {
        String confirmationLink = "http://192.168.1.65:8083/users/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirm your Garage Account");
        message.setText("Hello,\n\n" +
                "Welcome to Garage App! Please confirm your registration by clicking the link below:\n" +
                confirmationLink + "\n\n" +
                "If you did not register, please ignore this email.\n\n" +
                "Regards,\nGarage Team");

        javaMailSender.send(message);
    }
    @Override
    public boolean confirmUser(String token) {
        try {
            String email = jwtUtil.extractEmail(token);

            Optional<User> optionalUser = usersRepository.findByEmail(email);
            if (optionalUser.isEmpty()) return false;

            User user = optionalUser.get();

            if (user.isEnabled()) return true;

            user.setEnabled(true);
            usersRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User loginUser(String email, String password) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    @Override
    public User resetPassword(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email does not exist"));
    }

    @Override
    public void sendResetEmail(String email, String token) {
        String resetUrl = "http://192.168.1.65:8083/reset-password?token=" + token;
        String subject = "Reset Your Password";
        String body = "Hello,\n\n" +
                "You requested to reset your password. Please click the link below:\n" +
                resetUrl + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\nGarage Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Override
    public User updatePassword(String token, String newPassword) {
        String email = jwtUtil.extractEmailFromToken(token);

        if (email == null || !jwtUtil.validateResetPasswordToken(token, email)) {
            throw new RuntimeException("Invalid or expired reset token");
        }

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        user.setPassword(newPassword);
        usersRepository.save(user);

        return user;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User user) {
        return usersRepository.findById(id).map(existingUser -> {
            if (user.getSecondname() != null) existingUser.setSecondname(user.getSecondname());
            if (user.getFirstname() != null) existingUser.setFirstname(user.getFirstname());
            if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
            if (user.getPassword() != null) existingUser.setPassword(user.getPassword());
            if (user.getRole() != null) existingUser.setRole(user.getRole());
            if (user.getPhoneNumber() != null) existingUser.setPhoneNumber(user.getPhoneNumber());
            return usersRepository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("User with id " + id + " not found."));
    }

    @Override
    public void deleteUser(Long id) {
        if (!usersRepository.existsById(id)) {
            throw new RuntimeException("User with id " + id + " not found");
        }
        usersRepository.deleteById(id);
    }
}
