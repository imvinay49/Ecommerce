package com.vinayuttekar.ecommerce.service;

import com.vinayuttekar.ecommerce.dto.response.AddressResponse;
import com.vinayuttekar.ecommerce.entity.User;

import java.util.List;

public interface AddressService {
    AddressResponse createAddress(AddressResponse addressResponse, User user);
    List<AddressResponse> getAddresses();
    AddressResponse getAddressesById(Long id);
    AddressResponse getAddressesByIdForUser(Long id, User user);
    List<AddressResponse> getAddressesByUser(User user);
    AddressResponse updateAddress(Long addressId, AddressResponse addressResponse, User user);
    String deleteAddress(Long addressId, User user);
}
