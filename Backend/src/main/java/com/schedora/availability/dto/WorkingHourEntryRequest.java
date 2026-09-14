package com.schedora.availability.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record WorkingHourEntryRequest(
        @NotNull DayOfWeek dayOfWeek,
        LocalTime opensAt,
        LocalTime closesAt,
        boolean closed
) {
}
