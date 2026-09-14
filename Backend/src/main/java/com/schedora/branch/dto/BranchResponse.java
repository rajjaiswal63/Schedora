package com.schedora.branch.dto;

import com.schedora.branch.entity.BranchStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BranchResponse(
        Long id,
        Long salonId,
        String name,
        String phone,
        String addressLine,
        String city,
        String state,
        String pincode,
        BigDecimal latitude,
        BigDecimal longitude,
        BranchStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
