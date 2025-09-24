package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    public UserServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
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
