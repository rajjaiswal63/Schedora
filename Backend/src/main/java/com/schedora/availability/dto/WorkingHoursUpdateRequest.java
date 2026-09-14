package com.schedora.availability.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record WorkingHoursUpdateRequest(
        @NotNull Long branchId,
        @NotEmpty @Size(max = 7) List<@Valid WorkingHourEntryRequest> entries
) {
}
