package com.schedora.availability.dto;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record WorkingHourResponse(
        Long id,
        Long salonId,
        Long branchId,
        DayOfWeek dayOfWeek,
        LocalTime opensAt,
        LocalTime closesAt,
        boolean closed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
