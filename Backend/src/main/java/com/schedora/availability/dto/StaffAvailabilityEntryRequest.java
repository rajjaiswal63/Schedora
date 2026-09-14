package com.schedora.availability.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record StaffAvailabilityEntryRequest(
        @NotNull DayOfWeek dayOfWeek,
        LocalTime availableFrom,
        LocalTime availableTo,
        boolean unavailable
) {
}
