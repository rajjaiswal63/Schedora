package com.schedora.salon.dto;

import com.schedora.salon.entity.Salon;

public final class SalonMapper {
    private SalonMapper() {
    }

    public static SalonResponse toResponse(Salon salon) {
        return new SalonResponse(
                salon.getId(),
                salon.getOwner().getId(),
                salon.getOwner().getName(),
                salon.getOwner().getEmail(),
                salon.getName(),
                salon.getDescription(),
                salon.getPhone(),
                salon.getEmail(),
                salon.getAddressLine(),
                salon.getCity(),
                salon.getState(),
                salon.getPincode(),
                salon.getLatitude(),
                salon.getLongitude(),
                salon.getStatus(),
                salon.getCreatedAt(),
                salon.getUpdatedAt(),
                salon.getApprovedAt(),
                salon.getApprovedBy() == null ? null : salon.getApprovedBy().getId(),
                salon.getRejectedAt(),
                salon.getRejectedBy() == null ? null : salon.getRejectedBy().getId(),
                salon.getRejectionReason(),
                salon.getSuspendedAt(),
                salon.getSuspensionReason()
        );
    }
}
