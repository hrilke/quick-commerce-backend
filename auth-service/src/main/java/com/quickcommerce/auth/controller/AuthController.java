package com.quickcommerce.auth.controller;

import com.quickcommerce.auth.dto.*;
import com.quickcommerce.auth.entity.*;
import com.quickcommerce.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository; // minimal for now

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // NOTE: Placeholder logic (no hashing yet) - DO NOT use in production
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(AuthResponse.builder().message("email_taken").build());
        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash("{plain}" + request.getPassword()) // placeholder tag
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(AuthResponse.builder().message("registered").build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Placeholder: just acknowledge - real password check & token issuance deferred
        return ResponseEntity.ok(AuthResponse.builder().message("login_not_implemented").build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@RequestParam("email") String email) {
        return userRepository.findByEmail(email)
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .roles(u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                        .permissions(u.getRoles().stream()
                                .flatMap(r -> r.getPermissions().stream())
                                .map(p -> p.getName())
                                .collect(Collectors.toSet()))
                        .build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
