package com.schedora.availability.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record StaffAvailabilityUpdateRequest(
        @NotEmpty @Size(max = 7) List<@Valid StaffAvailabilityEntryRequest> entries
) {
}
