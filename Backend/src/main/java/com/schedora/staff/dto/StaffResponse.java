package com.schedora.staff.dto;

import com.schedora.staff.entity.StaffStatus;
import java.time.LocalDateTime;
import java.util.List;

public record StaffResponse(
        Long id,
        Long salonId,
        Long branchId,
        String branchName,
        String name,
        String phone,
        String email,
        String designation,
        StaffStatus status,
        List<StaffServiceSummary> services,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
