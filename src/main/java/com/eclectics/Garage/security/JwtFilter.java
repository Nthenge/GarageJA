package com.eclectics.Garage.security;

import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.UsersRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.MalformedJwtException;

import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;


    public JwtFilter(JwtUtil jwtUtil, UsersRepository usersRepository) {
        this.jwtUtil = jwtUtil;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws java.io.IOException, jakarta.servlet.ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String encryptedToken = authHeader.substring(7);
            String decryptedToken = null;

            try {
                decryptedToken = TokenEncryptor.decrypt(encryptedToken);
            } catch (Exception e) {
                System.err.println("Decryption failed for token: " + e.getMessage());
            }

            if (decryptedToken != null) {
                try {
                    String email = jwtUtil.extractEmail(decryptedToken);
                    String role = jwtUtil.extractRole(decryptedToken);

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        User user = usersRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                        List<SimpleGrantedAuthority> authorities = List.of(authority);

                        if (jwtUtil.validateToken(decryptedToken, email)) {

                            CustomUserDetails userDetails = new CustomUserDetails(user);

                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities()
                                    );

                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }

                    }
                } catch (MalformedJwtException e) {
                    System.err.println("JWT validation failed after decryption: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Authentication process failed: " + e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}