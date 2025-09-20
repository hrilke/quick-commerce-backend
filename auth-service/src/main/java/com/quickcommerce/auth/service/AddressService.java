package com.quickcommerce.auth.service;

import com.quickcommerce.auth.dto.AddressDto;
import com.quickcommerce.auth.dto.AddressRequest;

import java.util.List;

public interface AddressService {
    AddressDto create(String email, AddressRequest request);
    List<AddressDto> list(String email, com.quickcommerce.auth.entity.AddressType type);
    boolean delete(String email, String id);
}
