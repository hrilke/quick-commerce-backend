package com.quickcommerce.auth.repository;

import com.quickcommerce.auth.entity.RefreshToken;
import com.quickcommerce.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUserAndRevokedAtIsNullAndExpiresAtAfter(User user, Instant now);
}
