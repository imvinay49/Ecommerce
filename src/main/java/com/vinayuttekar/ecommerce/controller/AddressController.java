package com.vinayuttekar.ecommerce.controller;

import com.vinayuttekar.ecommerce.dto.response.AddressResponse;
import com.vinayuttekar.ecommerce.entity.User;
import com.vinayuttekar.ecommerce.service.AddressService;
import com.vinayuttekar.ecommerce.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressResponse request) {
        User user = authUtil.loggedInUser();
        return new ResponseEntity<>(addressService.createAddress(request, user), HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        return ResponseEntity.ok(addressService.getAddressesByUser(authUtil.loggedInUser()));
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressesByIdForUser(id, authUtil.loggedInUser()));
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressResponse>> getUserAddress() {
        return ResponseEntity.ok(addressService.getAddressesByUser(authUtil.loggedInUser()));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressResponse request) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, request, authUtil.loggedInUser()));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.deleteAddress(addressId, authUtil.loggedInUser()));
    }
}
