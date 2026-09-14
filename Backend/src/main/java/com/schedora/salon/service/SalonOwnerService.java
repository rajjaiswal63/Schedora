package com.schedora.salon.service;

import com.schedora.auth.service.AuthenticatedUserService;
import com.schedora.common.exception.ForbiddenException;
import com.schedora.common.exception.ResourceNotFoundException;
import com.schedora.salon.dto.SalonMapper;
import com.schedora.salon.dto.SalonResponse;
import com.schedora.salon.dto.UpdateSalonRequest;
import com.schedora.salon.entity.Salon;
import com.schedora.salon.entity.SalonStatus;
import com.schedora.salon.repository.SalonRepository;
import com.schedora.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalonOwnerService {

    private final AuthenticatedUserService authenticatedUserService;
    private final SalonRepository salonRepository;

    public SalonOwnerService(AuthenticatedUserService authenticatedUserService, SalonRepository salonRepository) {
        this.authenticatedUserService = authenticatedUserService;
        this.salonRepository = salonRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse profile() {
        return UserResponse.from(authenticatedUserService.currentUser());
    }

    @Transactional(readOnly = true)
    public SalonResponse ownSalon() {
        return SalonMapper.toResponse(requireOwnedSalon());
    }

    @Transactional
    public SalonResponse updateOwnSalon(UpdateSalonRequest request) {
        Salon salon = requireOwnedSalon();
        ensureSalonCanBeManaged(salon);
        salon.setName(request.name().trim());
        salon.setDescription(request.description());
        salon.setPhone(request.phone().trim());
        salon.setEmail(request.email().trim().toLowerCase());
        salon.setAddressLine(request.addressLine().trim());
        salon.setCity(request.city().trim());
        salon.setState(request.state().trim());
        salon.setPincode(request.pincode().trim());
        salon.setLatitude(request.latitude());
        salon.setLongitude(request.longitude());
        return SalonMapper.toResponse(salon);
    }

    public Salon requireOwnedSalon() {
        Long ownerId = authenticatedUserService.currentUser().getId();
        return salonRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon application not found for current owner"));
    }

    public void ensureSalonCanBeManaged(Salon salon) {
        if (salon.getStatus() == SalonStatus.REJECTED || salon.getStatus() == SalonStatus.SUSPENDED) {
            throw new ForbiddenException("This salon cannot be modified while it is " + salon.getStatus());
        }
    }
}
