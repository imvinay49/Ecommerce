package com.vinayuttekar.ecommerce.service.impl;

import com.vinayuttekar.ecommerce.dto.request.LoginRequest;
import com.vinayuttekar.ecommerce.dto.request.SignupRequest;
import com.vinayuttekar.ecommerce.dto.request.UpdateRolesRequest;
import com.vinayuttekar.ecommerce.dto.response.AuthResponse;
import com.vinayuttekar.ecommerce.entity.AppRole;
import com.vinayuttekar.ecommerce.entity.Role;
import com.vinayuttekar.ecommerce.entity.User;
import com.vinayuttekar.ecommerce.exception.ResourceAlreadyExistsException;
import com.vinayuttekar.ecommerce.exception.ResourceNotFoundException;
import com.vinayuttekar.ecommerce.repository.RoleRepository;
import com.vinayuttekar.ecommerce.repository.UserRepository;
import com.vinayuttekar.ecommerce.security.JwtUtils;
import com.vinayuttekar.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;

    @Override
    public void register(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResourceAlreadyExistsException("User", "email", email);
        }
        if (userRepository.existsByUserNameIgnoreCase(request.getUserName().trim())) {
            throw new ResourceAlreadyExistsException("User", "username", request.getUserName());
        }
        User user = new User();
        user.setUserName(request.getUserName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));
        user.getRoles().add(userRole);
        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(Locale.ROOT), request.getPassword()));
        return new AuthResponse(jwtUtils.generateToken(authentication));
    }

    @Override
    public void updateUserRoles(Long userId, UpdateRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Set<Role> roles = request.getRoles().stream().map(roleName -> {
            try {
                AppRole appRole = AppRole.valueOf(roleName.trim().toUpperCase(Locale.ROOT));
                return roleRepository.findByRoleName(appRole)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + roleName);
            }
        }).collect(Collectors.toSet());
        user.setRoles(roles);
        userRepository.save(user);
    }
}
