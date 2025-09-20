package com.quickcommerce.auth.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class UserDto {
    UUID id;
    String email;
    Set<String> roles;
    Set<String> permissions;
}
