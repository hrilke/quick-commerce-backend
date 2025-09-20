package com.quickcommerce.auth.controller;

import com.quickcommerce.auth.dto.*;
import com.quickcommerce.auth.entity.*;
import com.quickcommerce.auth.repository.AddressRepository;
import com.quickcommerce.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @PostMapping
    public ResponseEntity<AddressDto> create(@RequestParam("email") String email,
                                             @Valid @RequestBody AddressRequest request) {
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
                    return ResponseEntity.ok(toDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam("email") String email,
                                  @RequestParam(value = "type", required = false) AddressType type) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    var list = (type == null ?
                            addressRepository.findAllByUser(user) :
                            addressRepository.findAllByUserAndType(user, type))
                            .stream().map(this::toDto).toList();
                    return ResponseEntity.ok(list);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestParam("email") String email, @PathVariable("id") String id) {
        return userRepository.findByEmail(email)
                .map(user -> addressRepository.findById(java.util.UUID.fromString(id))
                        .filter(a -> a.getUser().getId().equals(user.getId()))
                        .map(a -> { addressRepository.delete(a); return ResponseEntity.noContent().build(); })
                        .orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
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