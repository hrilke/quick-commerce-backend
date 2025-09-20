package com.quickcommerce.auth.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String message;
    String accessToken;
    String tokenType;
    Long expiresIn;
    String refreshToken;
}
