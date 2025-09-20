package com.quickcommerce.auth.service.impl;

import com.quickcommerce.auth.entity.RefreshToken;
import com.quickcommerce.auth.entity.User;
import com.quickcommerce.auth.repository.RefreshTokenRepository;
import com.quickcommerce.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import com.quickcommerce.auth.util.PasswordUtils;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration-seconds:1209600}")
    private long refreshExpirationSeconds;

    @Override
    public RefreshToken createForUser(User user) {
        String token = UUID.randomUUID().toString() + UUID.randomUUID().toString();
        RefreshToken rt = RefreshToken.builder()
                .token(token)
                .tokenHash(PasswordUtils.sha256Hex(token))
                .user(user)
                .expiresAt(Instant.now().plusSeconds(refreshExpirationSeconds))
                .build();
        return repository.save(rt);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public void revoke(RefreshToken token) {
        repository.deleteByToken(token.getToken());
    }
}
