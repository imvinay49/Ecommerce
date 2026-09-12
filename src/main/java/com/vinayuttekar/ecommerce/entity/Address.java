package com.vinayuttekar.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street name should be atleast 5 Characters")
    private String street;

    @NotBlank
    @Size(min = 5, message = "Building name should be atleast 5 Characters")
    private String buildingName;

    @NotBlank
    @Size(min = 2, message = "State name should be atleast 5 Characters")
    private String state;

    @NotBlank
    @Size(min = 2, message = "City name should be atleast 5 Characters")
    private String city;

    @NotBlank
    @Size(min = 2, message = "Country name should be atleast 5 Characters")
    private String country;

    @NotBlank
    @Size(min = 5, message = "Pincode name should be atleast 5 Characters")
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String buildingName, String state, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
