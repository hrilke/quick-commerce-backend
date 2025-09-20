package com.quickcommerce.auth.dto;

import com.quickcommerce.auth.entity.AddressType;
import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder
public class AddressDto {
    UUID id;
    AddressType type;
    String receiverName;
    String line1;
    String line2;
    String city;
    String state;
    String postalCode;
    String country;
    String phone;
}
