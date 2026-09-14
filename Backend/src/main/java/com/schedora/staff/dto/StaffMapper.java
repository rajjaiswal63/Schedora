package com.schedora.staff.dto;

import com.schedora.offering.entity.ServiceOffering;
import com.schedora.staff.entity.Staff;
import java.util.Comparator;

public final class StaffMapper {
    private StaffMapper() {
    }

    public static StaffResponse toResponse(Staff staff) {
        return new StaffResponse(
                staff.getId(),
                staff.getSalon().getId(),
                staff.getBranch().getId(),
                staff.getBranch().getName(),
                staff.getName(),
                staff.getPhone(),
                staff.getEmail(),
                staff.getDesignation(),
                staff.getStatus(),
                staff.getServices().stream()
                        .sorted(Comparator.comparing(ServiceOffering::getId))
                        .map(service -> new StaffServiceSummary(service.getId(), service.getName()))
                        .toList(),
                staff.getCreatedAt(),
                staff.getUpdatedAt()
        );
    }
}
