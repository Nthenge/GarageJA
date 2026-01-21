package com.eclectics.Garage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private  String SECRET;
    private final long SESSION_EXPIRATION_TIME = 1000 * 60 * 60;
    private final long RESET_CONFIRM_TIME = 1000 * 60 * 15;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email, String role) {
       String jwt = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + SESSION_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
      return TokenEncryptor.encrypt(jwt);
    }

    public String generateEmailConfirmToken(String email, String role) {
       String jwt = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_CONFIRM_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        return TokenEncryptor.encrypt(jwt);
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
        } catch (io.jsonwebtoken.SignatureException se) {
            logger.error("JWT Error: Invalid signature or key mismatch during email extraction: {}", se.getMessage());
            return null;
        } catch (io.jsonwebtoken.MalformedJwtException mje) {
            logger.error("JWT Error: Token is malformed/corrupted: {}", mje.getMessage());
            return null;
        } catch (Exception e) {
            // Catch all others (e.g., ExpiredJwtException, etc.)
            logger.error("JWT Error: General error during email extraction: {}", e.getMessage());
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
        String  jwt = Jwts.builder()
                .setSubject(email)
                .claim("type", "reset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_CONFIRM_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        return TokenEncryptor.encrypt(jwt);
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
