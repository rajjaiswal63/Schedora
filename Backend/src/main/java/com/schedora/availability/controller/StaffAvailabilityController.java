package com.schedora.availability.controller;

import com.schedora.availability.dto.StaffAvailabilityResponse;
import com.schedora.availability.dto.StaffAvailabilityUpdateRequest;
import com.schedora.availability.service.StaffAvailabilityService;
import com.schedora.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salon-owner/staff/{staffId}/availability")
public class StaffAvailabilityController {

    private final StaffAvailabilityService staffAvailabilityService;

    public StaffAvailabilityController(StaffAvailabilityService staffAvailabilityService) {
        this.staffAvailabilityService = staffAvailabilityService;
    }

    @GetMapping
    public ApiResponse<List<StaffAvailabilityResponse>> getAvailability(@PathVariable Long staffId) {
        return ApiResponse.ok("Staff availability loaded", staffAvailabilityService.getAvailability(staffId));
    }

    @PutMapping
    public ApiResponse<List<StaffAvailabilityResponse>> updateAvailability(
            @PathVariable Long staffId,
            @Valid @RequestBody StaffAvailabilityUpdateRequest request
    ) {
        return ApiResponse.ok("Staff availability updated",
                staffAvailabilityService.updateAvailability(staffId, request));
    }
}
