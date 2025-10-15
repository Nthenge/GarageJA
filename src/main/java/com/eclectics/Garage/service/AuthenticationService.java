package com.eclectics.Garage.service;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.security.CustomUserDetails;
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
            throw new RuntimeException("User is not authenticated");
        }

        if (!(auth.getPrincipal() instanceof CustomUserDetails)) {
            logger.error("Authentication principal is not of type CustomUserDetails. Principal: {}", auth.getPrincipal());
            throw new RuntimeException("User is not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        logger.debug("Authenticated user ID: {}", userDetails.getId());

        User user = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found in the database.", userDetails.getId());
                    return new RuntimeException("User not found");
                });

        logger.info("Successfully retrieved authenticated user: {}", user.getEmail());
        return user;
    }
}
