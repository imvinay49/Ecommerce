package com.vinayuttekar.ecommerce.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long addressId;

    @NotBlank @Size(min = 5, max = 150)
    private String street;

    @NotBlank @Size(min = 2, max = 100)
    private String buildingName;

    @NotBlank @Size(min = 2, max = 50)
    private String state;

    @NotBlank @Size(min = 2, max = 50)
    private String country;

    @NotBlank @Size(min = 2, max = 50)
    private String city;

    @NotBlank @Pattern(regexp = "\\d{5,10}", message = "Pincode must contain 5 to 10 digits")
    private String pincode;
}
