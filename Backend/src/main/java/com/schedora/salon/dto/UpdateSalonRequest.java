package com.schedora.salon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateSalonRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 1000) String description,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 220) String addressLine,
        @NotBlank @Size(max = 80) String city,
        @NotBlank @Size(max = 80) String state,
        @NotBlank @Size(max = 20) String pincode,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
