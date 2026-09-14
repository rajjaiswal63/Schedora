package com.schedora.salon.dto;

import jakarta.validation.constraints.Size;

public record SalonStatusChangeRequest(
        @Size(max = 500) String reason
) {
}
