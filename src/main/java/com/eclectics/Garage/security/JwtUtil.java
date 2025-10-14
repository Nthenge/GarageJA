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

    private final String SECRET = "Garageojbv6789cvbn@65ghjpqowieurytmnvshdhwAJ";
    private final long SESSION_EXPIRATION_TIME = 1000 * 60 * 60;
    private final long RESET_CONFIRM_TIME = 1000 * 60 * 15;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email, String role) {
//       String jwt =
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + SESSION_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
//      return TokenEncryptor.encrypt(jwt);
    }

    public String generateEmailConfirmToken(String email, String role) {
//        String jwt =
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_CONFIRM_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
//        return TokenEncryptor.encrypt(jwt);
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    public String extractEmailFromToken(String token) {
        try {
            return extractEmail(token);
        } catch (Exception e) {
            return null;
        }
    }

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

    public String generateResetPasswordToken(String email){
//        String  jwt =
        return   Jwts.builder()
                .setSubject(email)
                .claim("type", "reset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_CONFIRM_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
//        return TokenEncryptor.encrypt(jwt);
    }


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
