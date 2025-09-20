package com.quickcommerce.auth.service.impl;

import com.quickcommerce.auth.dto.*;
import com.quickcommerce.auth.entity.User;
import com.quickcommerce.auth.entity.UserStatus;
import com.quickcommerce.auth.repository.UserRepository;
import com.quickcommerce.auth.repository.RoleRepository;
import com.quickcommerce.auth.service.AuthService;
import com.quickcommerce.auth.util.PasswordUtils;
import com.quickcommerce.auth.security.JwtUtil;
import com.quickcommerce.auth.service.RefreshTokenService;
import com.quickcommerce.auth.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder().message("email_taken").build();
        }
        String hash = PasswordUtils.sha256Hex(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash("{sha256}" + hash)
                .status(UserStatus.ACTIVE)
                .build();
        roleRepository.findByName("USER").ifPresent(r -> user.getRoles().add(r));
        userRepository.save(user);
        return AuthResponse.builder().message("registered").build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(u -> {
                    String stored = u.getPasswordHash();
                    if (stored != null && stored.startsWith("{sha256}")) {
                        String storedHash = stored.substring("{sha256}".length());
                        String attemptHash = PasswordUtils.sha256Hex(request.getPassword());
                        if (storedHash.equals(attemptHash)) {
                            List<String> roles = u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
                            String token = jwtUtil.generateToken(u.getEmail(), roles);
                            RefreshToken rt = refreshTokenService.createForUser(u);
                            return AuthResponse.builder()
                                    .message("login_success")
                                    .accessToken(token)
                                    .tokenType("Bearer")
                                    .expiresIn(jwtUtil.getExpirationSeconds())
                                    .refreshToken(rt.getToken())
                                    .build();
                        }
                    }
                    return AuthResponse.builder().message("invalid_credentials").build();
                })
                .orElse(AuthResponse.builder().message("invalid_credentials").build());
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .filter(rt -> rt.getExpiresAt().isAfter(java.time.Instant.now()))
                .map(rt -> {
                    var u = rt.getUser();
                    List<String> roles = u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
                    String token = jwtUtil.generateToken(u.getEmail(), roles);
                    return AuthResponse.builder()
                            .message("refresh_success")
                            .accessToken(token)
                            .tokenType("Bearer")
                            .expiresIn(jwtUtil.getExpirationSeconds())
                            .build();
                })
                .orElse(AuthResponse.builder().message("invalid_refresh_token").build());
    }

    @Override
    public UserDto me(String email) {
        return userRepository.findByEmail(email)
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .roles(u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                        .permissions(java.util.Collections.emptySet())
                        .build())
                .orElse(null);
    }
}
