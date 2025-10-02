package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.JwtUtil;
import com.eclectics.Garage.service.UserService;
import org.mapstruct.control.MappingControl;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private JavaMailSender javaMailSender;
    private JwtUtil jwtUtil;


    public UserServiceImpl(UsersRepository usersRepository, JavaMailSender javaMailSender,JwtUtil jwtUtil) {
        this.usersRepository = usersRepository;
        this.javaMailSender = javaMailSender;
        this.jwtUtil = jwtUtil;

    }

    @Override
    public User createUser(User user) {
        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }

        return usersRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password){
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        if (!user.getPassword().equals(password)){
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    @Override
    public User resetPassword(String email) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Email does not exist"));
        return user;
    }

    @Override
    public void sendResetEmail(String email, String token) {
        String resetUrl = "http://10.20.33.60/reset-password?token=" + token;
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
        }).orElseThrow(() -> new RuntimeException("User with id " + id + ", not found."));
    }

    @Override
    public void deleteUser(Long id) {
        if (!usersRepository.existsById(id)){
            throw new RuntimeException("User with id "+id+", not found");
        }
        usersRepository.deleteById(id);
    }

}
