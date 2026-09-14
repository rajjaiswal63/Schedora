package com.schedora.salon.dto;

import com.schedora.salon.entity.SalonStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalonResponse(
        Long id,
        Long ownerId,
        String ownerName,
        String ownerEmail,
        String name,
        String description,
        String phone,
        String email,
        String addressLine,
        String city,
        String state,
        String pincode,
        BigDecimal latitude,
        BigDecimal longitude,
        SalonStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime approvedAt,
        Long approvedById,
        LocalDateTime rejectedAt,
        Long rejectedById,
        String rejectionReason,
        LocalDateTime suspendedAt,
        String suspensionReason
) {
}
