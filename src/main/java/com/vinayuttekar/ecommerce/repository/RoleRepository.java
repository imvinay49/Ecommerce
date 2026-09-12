package com.vinayuttekar.ecommerce.repository;

import com.vinayuttekar.ecommerce.entity.Role;
import com.vinayuttekar.ecommerce.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByRoleName(AppRole roleName);
}