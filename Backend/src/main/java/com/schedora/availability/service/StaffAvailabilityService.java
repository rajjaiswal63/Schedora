package com.schedora.availability.service;

import com.schedora.availability.dto.StaffAvailabilityEntryRequest;
import com.schedora.availability.dto.StaffAvailabilityResponse;
import com.schedora.availability.dto.StaffAvailabilityUpdateRequest;
import com.schedora.availability.entity.StaffAvailability;
import com.schedora.availability.repository.StaffAvailabilityRepository;
import com.schedora.common.exception.BadRequestException;
import com.schedora.salon.service.SalonOwnerService;
import com.schedora.staff.entity.Staff;
import com.schedora.staff.service.StaffService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffAvailabilityService {

    private final StaffAvailabilityRepository availabilityRepository;
    private final StaffService staffService;
    private final SalonOwnerService salonOwnerService;

    public StaffAvailabilityService(StaffAvailabilityRepository availabilityRepository,
                                    StaffService staffService,
                                    SalonOwnerService salonOwnerService) {
        this.availabilityRepository = availabilityRepository;
        this.staffService = staffService;
        this.salonOwnerService = salonOwnerService;
    }

    @Transactional(readOnly = true)
    public List<StaffAvailabilityResponse> getAvailability(Long staffId) {
        Staff staff = staffService.requireOwnedStaff(staffId);
        return availabilityRepository.findByStaffIdAndSalonIdOrderByDayOfWeekAsc(staff.getId(), staff.getSalon().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<StaffAvailabilityResponse> updateAvailability(Long staffId, StaffAvailabilityUpdateRequest request) {
        Staff staff = staffService.requireOwnedStaff(staffId);
        salonOwnerService.ensureSalonCanBeManaged(staff.getSalon());
        validateUniqueDays(request.entries());

        for (StaffAvailabilityEntryRequest entry : request.entries()) {
            validateWindow(entry);
            StaffAvailability availability = availabilityRepository
                    .findByStaffIdAndSalonIdAndDayOfWeek(staff.getId(), staff.getSalon().getId(), entry.dayOfWeek())
                    .orElseGet(() -> new StaffAvailability(
                            staff.getSalon(),
                            staff.getBranch(),
                            staff,
                            entry.dayOfWeek(),
                            null,
                            null,
                            false
                    ));
            availability.setUnavailable(entry.unavailable());
            availability.setAvailableFrom(entry.unavailable() ? null : entry.availableFrom());
            availability.setAvailableTo(entry.unavailable() ? null : entry.availableTo());
            availabilityRepository.save(availability);
        }

        return getAvailability(staff.getId());
    }

    private void validateUniqueDays(List<StaffAvailabilityEntryRequest> entries) {
        Set<Object> days = new HashSet<>();
        for (StaffAvailabilityEntryRequest entry : entries) {
            if (!days.add(entry.dayOfWeek())) {
                throw new BadRequestException("Staff availability entries cannot contain duplicate days");
            }
        }
    }

    private void validateWindow(StaffAvailabilityEntryRequest entry) {
        if (entry.unavailable()) {
            return;
        }
        if (entry.availableFrom() == null || entry.availableTo() == null) {
            throw new BadRequestException("Available-from and available-to times are required");
        }
        if (!entry.availableFrom().isBefore(entry.availableTo())) {
            throw new BadRequestException("Available-from time must be before available-to time");
        }
    }

    private StaffAvailabilityResponse toResponse(StaffAvailability availability) {
        return new StaffAvailabilityResponse(
                availability.getId(),
                availability.getSalon().getId(),
                availability.getBranch().getId(),
                availability.getStaff().getId(),
                availability.getDayOfWeek(),
                availability.getAvailableFrom(),
                availability.getAvailableTo(),
                availability.isUnavailable(),
                availability.getCreatedAt(),
                availability.getUpdatedAt()
        );
    }
}
