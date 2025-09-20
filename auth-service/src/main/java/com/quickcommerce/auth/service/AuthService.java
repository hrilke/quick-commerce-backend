package com.quickcommerce.auth.service;

import com.quickcommerce.auth.dto.AuthResponse;
import com.quickcommerce.auth.dto.LoginRequest;
import com.quickcommerce.auth.dto.RegisterRequest;
import com.quickcommerce.auth.dto.UserDto;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserDto me(String email);
    AuthResponse refresh(String refreshToken);
}
