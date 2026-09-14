package com.schedora.branch.dto;

import com.schedora.branch.entity.Branch;

public final class BranchMapper {
    private BranchMapper() {
    }

    public static BranchResponse toResponse(Branch branch) {
        return new BranchResponse(
                branch.getId(),
                branch.getSalon().getId(),
                branch.getName(),
                branch.getPhone(),
                branch.getAddressLine(),
                branch.getCity(),
                branch.getState(),
                branch.getPincode(),
                branch.getLatitude(),
                branch.getLongitude(),
                branch.getStatus(),
                branch.getCreatedAt(),
                branch.getUpdatedAt()
        );
    }
}
