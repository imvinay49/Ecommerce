package com.vinayuttekar.ecommerce.repository;

import com.vinayuttekar.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserUserId(Long userId);
    boolean existsByAddressIdAndUserUserId(Long addressId, Long userId);
}
