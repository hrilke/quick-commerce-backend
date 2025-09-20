package com.quickcommerce.auth.service;

import com.quickcommerce.auth.entity.RefreshToken;
import com.quickcommerce.auth.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createForUser(User user);
    Optional<RefreshToken> findByToken(String token);
    void revoke(RefreshToken token);
}
