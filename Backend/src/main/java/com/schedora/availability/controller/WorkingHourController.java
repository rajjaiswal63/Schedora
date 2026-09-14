package com.schedora.availability.controller;

import com.schedora.availability.dto.WorkingHourResponse;
import com.schedora.availability.dto.WorkingHoursUpdateRequest;
import com.schedora.availability.service.WorkingHourService;
import com.schedora.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salon-owner/working-hours")
public class WorkingHourController {

    private final WorkingHourService workingHourService;

    public WorkingHourController(WorkingHourService workingHourService) {
        this.workingHourService = workingHourService;
    }

    @GetMapping
    public ApiResponse<List<WorkingHourResponse>> getWorkingHours(@RequestParam Long branchId) {
        return ApiResponse.ok("Working hours loaded", workingHourService.getWorkingHours(branchId));
    }

    @PutMapping
    public ApiResponse<List<WorkingHourResponse>> updateWorkingHours(
            @Valid @RequestBody WorkingHoursUpdateRequest request
    ) {
        return ApiResponse.ok("Working hours updated", workingHourService.updateWorkingHours(request));
    }
}
