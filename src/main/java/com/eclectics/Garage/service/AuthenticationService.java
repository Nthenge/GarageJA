package com.eclectics.Garage.service;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.CustomUserDetails;
import com.eclectics.Garage.exception.GarageExceptions.UnauthorizedException;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UsersRepository usersRepository;

    public AuthenticationService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User getCurrentUser() {
        logger.info("Fetching currently authenticated user...");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            logger.error("No authentication object found in security context.");
            throw new UnauthorizedException("User is not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        logger.debug("Authenticated user ID: {}", userDetails.getId());

        User user = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found in the database.", userDetails.getId());
                    return new ResourceNotFoundException("User not found");
                });

        logger.info("Successfully retrieved authenticated user: {}", user.getEmail());
        return user;
    }
}
