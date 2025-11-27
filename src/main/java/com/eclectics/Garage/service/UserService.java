package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.*;
import com.eclectics.Garage.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserRegistrationResponseDTO createUser(UserRegistrationRequestDTO user);
    Optional<UserRegistrationResponseDTO> getUserByEmail(String email);

    @Transactional(readOnly = true)
    List<UserRegistrationResponseDTO> filterUsers(
            String email,
            String firstname,
            String secondname
    );

    UserRegistrationResponseDTO updateUser(User user);
    UserLoginResponseDTO loginUser(UserLoginRequestDTO userLoginRequestDTO);
    UserPasswordResetResponseDTO resetPassword(UserPasswordResetRequestDTO resetRequestDTO);
    UserPasswordUpdateDTO updatePassword(UserPasswordUpdateDTO updatePassword);
    void confirmEmail(String token, String email);
    void  deleteUser(Long id);
    void deletePersonalAccount();
    void sendResetEmail(String email, String token);
    void suspendUser(Long userId);
    void unsuspendUser(Long userId);
    boolean confirmUser(String token);
}


