package com.schedora.offering.dto;

import com.schedora.offering.entity.ServiceOffering;

public final class ServiceOfferingMapper {
    private ServiceOfferingMapper() {
    }

    public static ServiceOfferingResponse toResponse(ServiceOffering offering) {
        return new ServiceOfferingResponse(
                offering.getId(),
                offering.getSalon().getId(),
                offering.getBranch().getId(),
                offering.getBranch().getName(),
                offering.getName(),
                offering.getDescription(),
                offering.getPrice(),
                offering.getDurationMinutes(),
                offering.getStatus(),
                offering.getCreatedAt(),
                offering.getUpdatedAt()
        );
    }
}
