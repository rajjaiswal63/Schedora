package com.schedora.availability.dto;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record StaffAvailabilityResponse(
        Long id,
        Long salonId,
        Long branchId,
        Long staffId,
        DayOfWeek dayOfWeek,
        LocalTime availableFrom,
        LocalTime availableTo,
        boolean unavailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
