package com.eclectics.Garage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "ThisIsASecretKeyThatIsAtLeast32CharsLong123";
    private final long EXPIRATION_TIME = 1000 * 60 * 60;
    private final long ResetPasswordTime = 1000 * 60 * 15;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Generate token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract role
    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    //Extract emails for reset
    public String extractEmailFromToken(String token) {
        try {
            return extractEmail(token);
        } catch (Exception e) {
            return null;
        }
    }

    //validate token for reset
    public boolean validateResetPasswordToken(String token, String email) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String tokenEmail = claims.getSubject();
            String type = claims.get("type", String.class);

            return tokenEmail.equals(email) &&
                    "reset".equals(type) &&
                    !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Generate forgot password token
    public String generateResetPasswordToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "reset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ResetPasswordTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token for login
    public boolean validateToken(String token, String email) {
        String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
