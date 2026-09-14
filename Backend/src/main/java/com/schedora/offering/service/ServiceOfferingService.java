package com.schedora.offering.service;

import com.schedora.branch.entity.Branch;
import com.schedora.branch.service.BranchService;
import com.schedora.common.exception.ForbiddenException;
import com.schedora.common.exception.ResourceNotFoundException;
import com.schedora.offering.dto.ServiceOfferingMapper;
import com.schedora.offering.dto.ServiceOfferingRequest;
import com.schedora.offering.dto.ServiceOfferingResponse;
import com.schedora.offering.entity.OfferingStatus;
import com.schedora.offering.entity.ServiceOffering;
import com.schedora.offering.repository.ServiceOfferingRepository;
import com.schedora.salon.entity.Salon;
import com.schedora.salon.service.SalonOwnerService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceOfferingService {

    private final ServiceOfferingRepository offeringRepository;
    private final SalonOwnerService salonOwnerService;
    private final BranchService branchService;

    public ServiceOfferingService(ServiceOfferingRepository offeringRepository,
                                  SalonOwnerService salonOwnerService,
                                  BranchService branchService) {
        this.offeringRepository = offeringRepository;
        this.salonOwnerService = salonOwnerService;
        this.branchService = branchService;
    }

    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> listServices() {
        Salon salon = salonOwnerService.requireOwnedSalon();
        return offeringRepository.findBySalonIdOrderByCreatedAtDesc(salon.getId()).stream()
                .map(ServiceOfferingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceOfferingResponse getService(Long serviceId) {
        return ServiceOfferingMapper.toResponse(requireOwnedService(serviceId));
    }

    @Transactional
    public ServiceOfferingResponse createService(ServiceOfferingRequest request) {
        Branch branch = branchService.requireOwnedBranch(request.branchId());
        salonOwnerService.ensureSalonCanBeManaged(branch.getSalon());
        ServiceOffering offering = new ServiceOffering(
                branch.getSalon(),
                branch,
                request.name().trim(),
                request.description(),
                request.price(),
                request.durationMinutes()
        );
        return ServiceOfferingMapper.toResponse(offeringRepository.save(offering));
    }

    @Transactional
    public ServiceOfferingResponse updateService(Long serviceId, ServiceOfferingRequest request) {
        ServiceOffering offering = requireOwnedService(serviceId);
        Branch branch = branchService.requireOwnedBranch(request.branchId());
        if (!offering.getSalon().getId().equals(branch.getSalon().getId())) {
            throw new ForbiddenException("Service and branch must belong to the same salon");
        }
        salonOwnerService.ensureSalonCanBeManaged(offering.getSalon());
        offering.setBranch(branch);
        offering.setName(request.name().trim());
        offering.setDescription(request.description());
        offering.setPrice(request.price());
        offering.setDurationMinutes(request.durationMinutes());
        return ServiceOfferingMapper.toResponse(offering);
    }

    @Transactional
    public ServiceOfferingResponse deactivateService(Long serviceId) {
        ServiceOffering offering = requireOwnedService(serviceId);
        salonOwnerService.ensureSalonCanBeManaged(offering.getSalon());
        offering.setStatus(OfferingStatus.INACTIVE);
        return ServiceOfferingMapper.toResponse(offering);
    }

    public ServiceOffering requireOwnedService(Long serviceId) {
        Salon salon = salonOwnerService.requireOwnedSalon();
        ServiceOffering offering = offeringRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!offering.getSalon().getId().equals(salon.getId())) {
            throw new ForbiddenException("Service does not belong to the authenticated salon owner");
        }
        return offering;
    }
}
