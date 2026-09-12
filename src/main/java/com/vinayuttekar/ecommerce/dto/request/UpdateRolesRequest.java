package com.vinayuttekar.ecommerce.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateRolesRequest {

    @NotEmpty(message = "At least one role is required")
    private Set<String> roles;
}
