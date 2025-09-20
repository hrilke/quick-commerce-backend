package com.quickcommerce.auth.service.impl;

import com.quickcommerce.auth.dto.AddressDto;
import com.quickcommerce.auth.dto.AddressRequest;
import com.quickcommerce.auth.entity.Address;
import com.quickcommerce.auth.entity.AddressType;
import com.quickcommerce.auth.repository.AddressRepository;
import com.quickcommerce.auth.repository.UserRepository;
import com.quickcommerce.auth.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public AddressDto create(String email, AddressRequest request) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    Address address = Address.builder()
                            .user(user)
                            .type(request.getType())
                            .receiverName(request.getReceiverName())
                            .line1(request.getLine1())
                            .line2(request.getLine2())
                            .city(request.getCity())
                            .state(request.getState())
                            .postalCode(request.getPostalCode())
                            .country(request.getCountry())
                            .phone(request.getPhone())
                            .build();
                    Address saved = addressRepository.save(address);
                    return toDto(saved);
                })
                .orElse(null);
    }

    @Override
    public List<AddressDto> list(String email, AddressType type) {
        return userRepository.findByEmail(email)
                .map(user -> (type == null ?
                        addressRepository.findAllByUser(user) :
                        addressRepository.findAllByUserAndType(user, type))
                        .stream().map(this::toDto).toList())
                .orElse(List.of());
    }

    @Override
    public boolean delete(String email, String id) {
        return userRepository.findByEmail(email)
                .map(user -> addressRepository.findById(java.util.UUID.fromString(id))
                        .filter(a -> a.getUser().getId().equals(user.getId()))
                        .map(a -> {
                            addressRepository.delete(a);
                            return true;
                        })
                        .orElse(false))
                .orElse(false);
    }

    private AddressDto toDto(Address a) {
        return AddressDto.builder()
                .id(a.getId())
                .type(a.getType())
                .receiverName(a.getReceiverName())
                .line1(a.getLine1())
                .line2(a.getLine2())
                .city(a.getCity())
                .state(a.getState())
                .postalCode(a.getPostalCode())
                .country(a.getCountry())
                .phone(a.getPhone())
                .build();
    }
}
