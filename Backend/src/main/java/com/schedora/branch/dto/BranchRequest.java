package com.schedora.branch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record BranchRequest(
        @NotBlank @Size(max = 140) String name,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(max = 220) String addressLine,
        @NotBlank @Size(max = 80) String city,
        @NotBlank @Size(max = 80) String state,
        @NotBlank @Size(max = 20) String pincode,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
