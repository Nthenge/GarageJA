package com.eclectics.Garage.service;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UsersRepository usersRepository;

    public AuthenticationService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("User is not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        return usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
