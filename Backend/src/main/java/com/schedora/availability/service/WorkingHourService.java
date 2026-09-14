package com.schedora.availability.service;

import com.schedora.availability.dto.WorkingHourEntryRequest;
import com.schedora.availability.dto.WorkingHourResponse;
import com.schedora.availability.dto.WorkingHoursUpdateRequest;
import com.schedora.availability.entity.WorkingHour;
import com.schedora.availability.repository.WorkingHourRepository;
import com.schedora.branch.entity.Branch;
import com.schedora.branch.service.BranchService;
import com.schedora.common.exception.BadRequestException;
import com.schedora.salon.service.SalonOwnerService;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkingHourService {

    private final WorkingHourRepository workingHourRepository;
    private final BranchService branchService;
    private final SalonOwnerService salonOwnerService;

    public WorkingHourService(WorkingHourRepository workingHourRepository,
                              BranchService branchService,
                              SalonOwnerService salonOwnerService) {
        this.workingHourRepository = workingHourRepository;
        this.branchService = branchService;
        this.salonOwnerService = salonOwnerService;
    }

    @Transactional(readOnly = true)
    public List<WorkingHourResponse> getWorkingHours(Long branchId) {
        Branch branch = branchService.requireOwnedBranch(branchId);
        return workingHourRepository.findByBranchIdAndSalonIdOrderByDayOfWeekAsc(branch.getId(), branch.getSalon().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<WorkingHourResponse> updateWorkingHours(WorkingHoursUpdateRequest request) {
        Branch branch = branchService.requireOwnedBranch(request.branchId());
        salonOwnerService.ensureSalonCanBeManaged(branch.getSalon());
        validateUniqueDays(request.entries());

        for (WorkingHourEntryRequest entry : request.entries()) {
            validateWindow(entry.closed(), entry.opensAt(), entry.closesAt());
            WorkingHour hour = workingHourRepository
                    .findByBranchIdAndSalonIdAndDayOfWeek(branch.getId(), branch.getSalon().getId(), entry.dayOfWeek())
                    .orElseGet(() -> new WorkingHour(branch.getSalon(), branch, entry.dayOfWeek(), null, null, false));
            hour.setClosed(entry.closed());
            hour.setOpensAt(entry.closed() ? null : entry.opensAt());
            hour.setClosesAt(entry.closed() ? null : entry.closesAt());
            workingHourRepository.save(hour);
        }

        return getWorkingHours(branch.getId());
    }

    private void validateUniqueDays(List<WorkingHourEntryRequest> entries) {
        Set<Object> days = new HashSet<>();
        for (WorkingHourEntryRequest entry : entries) {
            if (!days.add(entry.dayOfWeek())) {
                throw new BadRequestException("Working hour entries cannot contain duplicate days");
            }
        }
    }

    private void validateWindow(boolean closed, LocalTime start, LocalTime end) {
        if (closed) {
            return;
        }
        if (start == null || end == null) {
            throw new BadRequestException("Opening and closing time are required for working hours");
        }
        if (!start.isBefore(end)) {
            throw new BadRequestException("Opening time must be before closing time for working hours");
        }
    }

    private WorkingHourResponse toResponse(WorkingHour hour) {
        return new WorkingHourResponse(
                hour.getId(),
                hour.getSalon().getId(),
                hour.getBranch().getId(),
                hour.getDayOfWeek(),
                hour.getOpensAt(),
                hour.getClosesAt(),
                hour.isClosed(),
                hour.getCreatedAt(),
                hour.getUpdatedAt()
        );
    }
}
