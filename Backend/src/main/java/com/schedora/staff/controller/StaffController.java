package com.schedora.staff.controller;

import com.schedora.common.response.ApiResponse;
import com.schedora.staff.dto.StaffRequest;
import com.schedora.staff.dto.StaffResponse;
import com.schedora.staff.service.StaffService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salon-owner/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ApiResponse<List<StaffResponse>> listStaff() {
        return ApiResponse.ok("Staff loaded", staffService.listStaff());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(@Valid @RequestBody StaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Staff member created", staffService.createStaff(request)));
    }

    @GetMapping("/{staffId}")
    public ApiResponse<StaffResponse> getStaff(@PathVariable Long staffId) {
        return ApiResponse.ok("Staff member loaded", staffService.getStaff(staffId));
    }

    @PutMapping("/{staffId}")
    public ApiResponse<StaffResponse> updateStaff(
            @PathVariable Long staffId,
            @Valid @RequestBody StaffRequest request
    ) {
        return ApiResponse.ok("Staff member updated", staffService.updateStaff(staffId, request));
    }

    @DeleteMapping("/{staffId}")
    public ApiResponse<StaffResponse> deactivateStaff(@PathVariable Long staffId) {
        return ApiResponse.ok("Staff member deactivated", staffService.deactivateStaff(staffId));
    }
}
