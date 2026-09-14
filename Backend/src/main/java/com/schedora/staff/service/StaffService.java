package com.schedora.staff.service;

import com.schedora.branch.entity.Branch;
import com.schedora.branch.service.BranchService;
import com.schedora.common.exception.BadRequestException;
import com.schedora.common.exception.ForbiddenException;
import com.schedora.common.exception.ResourceNotFoundException;
import com.schedora.offering.entity.ServiceOffering;
import com.schedora.offering.repository.ServiceOfferingRepository;
import com.schedora.salon.entity.Salon;
import com.schedora.salon.service.SalonOwnerService;
import com.schedora.staff.dto.StaffMapper;
import com.schedora.staff.dto.StaffRequest;
import com.schedora.staff.dto.StaffResponse;
import com.schedora.staff.entity.Staff;
import com.schedora.staff.entity.StaffStatus;
import com.schedora.staff.repository.StaffRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final ServiceOfferingRepository offeringRepository;
    private final SalonOwnerService salonOwnerService;
    private final BranchService branchService;

    public StaffService(StaffRepository staffRepository,
                        ServiceOfferingRepository offeringRepository,
                        SalonOwnerService salonOwnerService,
                        BranchService branchService) {
        this.staffRepository = staffRepository;
        this.offeringRepository = offeringRepository;
        this.salonOwnerService = salonOwnerService;
        this.branchService = branchService;
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> listStaff() {
        Salon salon = salonOwnerService.requireOwnedSalon();
        return staffRepository.findBySalonIdOrderByCreatedAtDesc(salon.getId()).stream()
                .map(StaffMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StaffResponse getStaff(Long staffId) {
        return StaffMapper.toResponse(requireOwnedStaff(staffId));
    }

    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        Branch branch = branchService.requireOwnedBranch(request.branchId());
        salonOwnerService.ensureSalonCanBeManaged(branch.getSalon());
        Staff staff = new Staff(
                branch.getSalon(),
                branch,
                request.name().trim(),
                request.phone().trim(),
                blankToNull(request.email()),
                request.designation().trim()
        );
        staff.setServices(loadOwnedServices(branch.getSalon(), request.serviceIds()));
        return StaffMapper.toResponse(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse updateStaff(Long staffId, StaffRequest request) {
        Staff staff = requireOwnedStaff(staffId);
        Branch branch = branchService.requireOwnedBranch(request.branchId());
        if (!staff.getSalon().getId().equals(branch.getSalon().getId())) {
            throw new ForbiddenException("Staff and branch must belong to the same salon");
        }
        salonOwnerService.ensureSalonCanBeManaged(staff.getSalon());
        staff.setBranch(branch);
        staff.setName(request.name().trim());
        staff.setPhone(request.phone().trim());
        staff.setEmail(blankToNull(request.email()));
        staff.setDesignation(request.designation().trim());
        staff.setServices(loadOwnedServices(staff.getSalon(), request.serviceIds()));
        return StaffMapper.toResponse(staff);
    }

    @Transactional
    public StaffResponse deactivateStaff(Long staffId) {
        Staff staff = requireOwnedStaff(staffId);
        salonOwnerService.ensureSalonCanBeManaged(staff.getSalon());
        staff.setStatus(StaffStatus.INACTIVE);
        return StaffMapper.toResponse(staff);
    }

    public Staff requireOwnedStaff(Long staffId) {
        Salon salon = salonOwnerService.requireOwnedSalon();
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found"));
        if (!staff.getSalon().getId().equals(salon.getId())) {
            throw new ForbiddenException("Staff member does not belong to the authenticated salon owner");
        }
        return staff;
    }

    private Set<ServiceOffering> loadOwnedServices(Salon salon, Set<Long> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return new LinkedHashSet<>();
        }
        List<ServiceOffering> services = offeringRepository.findByIdInAndSalonId(serviceIds, salon.getId());
        if (services.size() != serviceIds.size()) {
            throw new BadRequestException("One or more services do not belong to this salon");
        }
        return new LinkedHashSet<>(services);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
