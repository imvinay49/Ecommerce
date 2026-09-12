package com.vinayuttekar.ecommerce.service.impl;

import com.vinayuttekar.ecommerce.dto.response.AddressResponse;
import com.vinayuttekar.ecommerce.entity.Address;
import com.vinayuttekar.ecommerce.entity.User;
import com.vinayuttekar.ecommerce.exception.ResourceNotFoundException;
import com.vinayuttekar.ecommerce.repository.AddressRepository;
import com.vinayuttekar.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;

    @Override
    public AddressResponse createAddress(AddressResponse response, User user) {
        Address address = modelMapper.map(response, Address.class);
        address.setUser(user);
        return modelMapper.map(addressRepository.save(address), AddressResponse.class);
    }

    @Override
    public List<AddressResponse> getAddresses() {
        return addressRepository.findAll().stream().map(a -> modelMapper.map(a, AddressResponse.class)).toList();
    }

    @Override
    public AddressResponse getAddressesById(Long id) {
        return modelMapper.map(findAddress(id), AddressResponse.class);
    }

    @Override
    public AddressResponse getAddressesByIdForUser(Long id, User user) {
        Address address = addressRepository.findById(id)
                .filter(a -> a.getUser() != null && a.getUser().getUserId().equals(user.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", id));
        return modelMapper.map(address, AddressResponse.class);
    }

    @Override
    public List<AddressResponse> getAddressesByUser(User user) {
        return addressRepository.findByUserUserId(user.getUserId()).stream()
                .map(a -> modelMapper.map(a, AddressResponse.class)).toList();
    }

    @Override
    public AddressResponse updateAddress(Long addressId, AddressResponse response, User user) {
        Address address = findOwnedAddress(addressId, user);
        address.setCity(response.getCity());
        address.setState(response.getState());
        address.setPincode(response.getPincode());
        address.setCountry(response.getCountry());
        address.setStreet(response.getStreet());
        address.setBuildingName(response.getBuildingName());
        return modelMapper.map(addressRepository.save(address), AddressResponse.class);
    }

    @Override
    public String deleteAddress(Long addressId, User user) {
        Address address = findOwnedAddress(addressId, user);
        addressRepository.delete(address);
        return "Address deleted successfully with addressId: " + addressId;
    }

    private Address findAddress(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", id));
    }

    private Address findOwnedAddress(Long id, User user) {
        Address address = findAddress(id);
        if (address.getUser() == null || !address.getUser().getUserId().equals(user.getUserId())) {
            throw new ResourceNotFoundException("Address", "addressId", id);
        }
        return address;
    }
}
