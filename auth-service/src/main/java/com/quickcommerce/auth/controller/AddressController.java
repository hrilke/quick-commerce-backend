package com.quickcommerce.auth.controller;

import com.quickcommerce.auth.dto.*;
import com.quickcommerce.auth.entity.*;
import com.quickcommerce.auth.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressDto> create(@RequestParam("email") String email,
                                             @Valid @RequestBody AddressRequest request) {
        var dto = addressService.create(email, request);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam("email") String email,
                                  @RequestParam(value = "type", required = false) AddressType type) {
        var list = addressService.list(email, type);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestParam("email") String email, @PathVariable("id") String id) {
        var ok = addressService.delete(email, id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}