package com.vinayuttekar.ecommerce.service;

import com.vinayuttekar.ecommerce.dto.request.LoginRequest;
import com.vinayuttekar.ecommerce.dto.request.SignupRequest;
import com.vinayuttekar.ecommerce.dto.request.UpdateRolesRequest;
import com.vinayuttekar.ecommerce.dto.response.AuthResponse;

public interface AuthService {

    void register(SignupRequest request);

    AuthResponse login(LoginRequest request);

    void updateUserRoles(Long userId, UpdateRolesRequest request);
}