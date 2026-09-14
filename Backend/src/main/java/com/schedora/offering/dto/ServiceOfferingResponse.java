package com.schedora.offering.dto;

import com.schedora.offering.entity.OfferingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceOfferingResponse(
        Long id,
        Long salonId,
        Long branchId,
        String branchName,
        String name,
        String description,
        BigDecimal price,
        Integer durationMinutes,
        OfferingStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
