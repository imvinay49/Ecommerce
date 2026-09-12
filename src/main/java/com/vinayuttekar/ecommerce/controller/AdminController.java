package com.vinayuttekar.ecommerce.controller;

import com.vinayuttekar.ecommerce.dto.request.UpdateRolesRequest;
import com.vinayuttekar.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRolesRequest request
    ) {

        authService.updateUserRoles(userId, request);

        return ResponseEntity.ok(
                "User roles updated successfully"
        );
    }
}