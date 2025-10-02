package com.eclectics.Garage.service;

import com.eclectics.Garage.model.Role;
import com.eclectics.Garage.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void  deleteUser(Long id);
    User loginUser(String email, String password);
    User resetPassword(String email);
    void sendResetEmail(String email, String token);
    User updatePassword(String token, String newPassword);
}


