package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Method to find the token by its unique string value
    Optional<PasswordResetToken> findByToken(String token);

    // Optional: Delete tokens that have expired
    // @Modifying
    // @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate <= :now")
    // void deleteExpiredTokens(@Param("now") Instant now);
}
