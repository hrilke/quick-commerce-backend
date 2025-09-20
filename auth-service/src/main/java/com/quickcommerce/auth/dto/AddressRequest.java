package com.quickcommerce.auth.dto;

import com.quickcommerce.auth.entity.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressRequest {
    @NotNull
    private AddressType type;
    @NotBlank
    private String receiverName;
    @NotBlank
    private String line1;
    private String line2;
    @NotBlank
    private String city;
    private String state;
    private String postalCode;
    @NotBlank
    private String country;
    private String phone;
}
