package com.schedora.branch.service;

import com.schedora.branch.dto.BranchMapper;
import com.schedora.branch.dto.BranchRequest;
import com.schedora.branch.dto.BranchResponse;
import com.schedora.branch.entity.Branch;
import com.schedora.branch.entity.BranchStatus;
import com.schedora.branch.repository.BranchRepository;
import com.schedora.common.exception.ForbiddenException;
import com.schedora.common.exception.ResourceNotFoundException;
import com.schedora.salon.entity.Salon;
import com.schedora.salon.service.SalonOwnerService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final SalonOwnerService salonOwnerService;

    public BranchService(BranchRepository branchRepository, SalonOwnerService salonOwnerService) {
        this.branchRepository = branchRepository;
        this.salonOwnerService = salonOwnerService;
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> listBranches() {
        Salon salon = salonOwnerService.requireOwnedSalon();
        return branchRepository.findBySalonIdOrderByCreatedAtDesc(salon.getId()).stream()
                .map(BranchMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BranchResponse getBranch(Long branchId) {
        return BranchMapper.toResponse(requireOwnedBranch(branchId));
    }

    @Transactional
    public BranchResponse createBranch(BranchRequest request) {
        Salon salon = salonOwnerService.requireOwnedSalon();
        salonOwnerService.ensureSalonCanBeManaged(salon);
        Branch branch = new Branch(
                salon,
                request.name().trim(),
                request.phone().trim(),
                request.addressLine().trim(),
                request.city().trim(),
                request.state().trim(),
                request.pincode().trim(),
                request.latitude(),
                request.longitude()
        );
        return BranchMapper.toResponse(branchRepository.save(branch));
    }

    @Transactional
    public BranchResponse updateBranch(Long branchId, BranchRequest request) {
        Branch branch = requireOwnedBranch(branchId);
        salonOwnerService.ensureSalonCanBeManaged(branch.getSalon());
        branch.setName(request.name().trim());
        branch.setPhone(request.phone().trim());
        branch.setAddressLine(request.addressLine().trim());
        branch.setCity(request.city().trim());
        branch.setState(request.state().trim());
        branch.setPincode(request.pincode().trim());
        branch.setLatitude(request.latitude());
        branch.setLongitude(request.longitude());
        return BranchMapper.toResponse(branch);
    }

    @Transactional
    public BranchResponse deactivateBranch(Long branchId) {
        Branch branch = requireOwnedBranch(branchId);
        salonOwnerService.ensureSalonCanBeManaged(branch.getSalon());
        branch.setStatus(BranchStatus.INACTIVE);
        return BranchMapper.toResponse(branch);
    }

    public Branch requireOwnedBranch(Long branchId) {
        Salon salon = salonOwnerService.requireOwnedSalon();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        if (!branch.getSalon().getId().equals(salon.getId())) {
            throw new ForbiddenException("Branch does not belong to the authenticated salon owner");
        }
        return branch;
    }
}
