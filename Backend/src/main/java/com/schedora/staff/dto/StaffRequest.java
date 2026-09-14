package com.schedora.staff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record StaffRequest(
        @NotNull Long branchId,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 30) String phone,
        @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 100) String designation,
        Set<Long> serviceIds
) {
}
