package com.schedora.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record SalonOwnerRegistrationRequest(
        @NotBlank @Size(max = 120) String ownerName,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(min = 8, max = 80) String password,
        @NotBlank @Size(max = 160) String salonName,
        @Size(max = 1000) String salonDescription,
        @NotBlank @Size(max = 30) String salonPhone,
        @NotBlank @Email @Size(max = 160) String salonEmail,
        @NotBlank @Size(max = 220) String addressLine,
        @NotBlank @Size(max = 80) String city,
        @NotBlank @Size(max = 80) String state,
        @NotBlank @Size(max = 20) String pincode,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
