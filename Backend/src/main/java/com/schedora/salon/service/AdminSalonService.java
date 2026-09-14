package com.schedora.salon.service;

import com.schedora.auth.service.AuthenticatedUserService;
import com.schedora.common.exception.InvalidStateTransitionException;
import com.schedora.common.exception.ResourceNotFoundException;
import com.schedora.salon.dto.SalonMapper;
import com.schedora.salon.dto.SalonResponse;
import com.schedora.salon.entity.Salon;
import com.schedora.salon.entity.SalonStatus;
import com.schedora.salon.repository.SalonRepository;
import com.schedora.user.entity.AppUser;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminSalonService {

    private final SalonRepository salonRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public AdminSalonService(SalonRepository salonRepository, AuthenticatedUserService authenticatedUserService) {
        this.salonRepository = salonRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional(readOnly = true)
    public List<SalonResponse> allSalons() {
        return salonRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(SalonMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalonResponse> pendingSalons() {
        return salonRepository.findByStatusOrderByCreatedAtDesc(SalonStatus.PENDING).stream()
                .map(SalonMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalonResponse salonDetails(Long salonId) {
        return SalonMapper.toResponse(requireSalon(salonId));
    }

    @Transactional
    public SalonResponse approve(Long salonId) {
        Salon salon = requireSalon(salonId);
        if (salon.getStatus() != SalonStatus.PENDING) {
            throw new InvalidStateTransitionException("Only pending salons can be approved");
        }
        salon.setStatus(SalonStatus.ACTIVE);
        salon.setApprovedAt(LocalDateTime.now());
        salon.setApprovedBy(currentAdmin());
        salon.setRejectedAt(null);
        salon.setRejectedBy(null);
        salon.setRejectionReason(null);
        return SalonMapper.toResponse(salon);
    }

    @Transactional
    public SalonResponse reject(Long salonId, String reason) {
        Salon salon = requireSalon(salonId);
        if (salon.getStatus() != SalonStatus.PENDING) {
            throw new InvalidStateTransitionException("Only pending salons can be rejected");
        }
        salon.setStatus(SalonStatus.REJECTED);
        salon.setRejectedAt(LocalDateTime.now());
        salon.setRejectedBy(currentAdmin());
        salon.setRejectionReason(blankToDefault(reason, "Rejected by super admin"));
        return SalonMapper.toResponse(salon);
    }

    @Transactional
    public SalonResponse suspend(Long salonId, String reason) {
        Salon salon = requireSalon(salonId);
        if (salon.getStatus() != SalonStatus.ACTIVE) {
            throw new InvalidStateTransitionException("Only active salons can be suspended");
        }
        salon.setStatus(SalonStatus.SUSPENDED);
        salon.setSuspendedAt(LocalDateTime.now());
        salon.setSuspensionReason(blankToDefault(reason, "Suspended by super admin"));
        return SalonMapper.toResponse(salon);
    }

    @Transactional
    public SalonResponse activate(Long salonId) {
        Salon salon = requireSalon(salonId);
        if (salon.getStatus() != SalonStatus.SUSPENDED) {
            throw new InvalidStateTransitionException("Only suspended salons can be activated");
        }
        salon.setStatus(SalonStatus.ACTIVE);
        salon.setSuspendedAt(null);
        salon.setSuspensionReason(null);
        return SalonMapper.toResponse(salon);
    }

    private Salon requireSalon(Long salonId) {
        return salonRepository.findWithOwnerById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found"));
    }

    private AppUser currentAdmin() {
        return authenticatedUserService.currentUser();
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
