package com.schedora.salon.controller;

import com.schedora.common.response.ApiResponse;
import com.schedora.salon.dto.SalonResponse;
import com.schedora.salon.dto.SalonStatusChangeRequest;
import com.schedora.salon.service.AdminSalonService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/salons")
public class AdminSalonController {

    private final AdminSalonService adminSalonService;

    public AdminSalonController(AdminSalonService adminSalonService) {
        this.adminSalonService = adminSalonService;
    }

    @GetMapping
    public ApiResponse<List<SalonResponse>> allSalons() {
        return ApiResponse.ok("Salons loaded", adminSalonService.allSalons());
    }

    @GetMapping("/pending")
    public ApiResponse<List<SalonResponse>> pendingSalons() {
        return ApiResponse.ok("Pending salons loaded", adminSalonService.pendingSalons());
    }

    @GetMapping("/{salonId}")
    public ApiResponse<SalonResponse> salonDetails(@PathVariable Long salonId) {
        return ApiResponse.ok("Salon loaded", adminSalonService.salonDetails(salonId));
    }

    @PatchMapping("/{salonId}/approve")
    public ApiResponse<SalonResponse> approve(@PathVariable Long salonId) {
        return ApiResponse.ok("Salon approved", adminSalonService.approve(salonId));
    }

    @PatchMapping("/{salonId}/reject")
    public ApiResponse<SalonResponse> reject(
            @PathVariable Long salonId,
            @Valid @RequestBody(required = false) SalonStatusChangeRequest request
    ) {
        return ApiResponse.ok("Salon rejected", adminSalonService.reject(salonId, reason(request)));
    }

    @PatchMapping("/{salonId}/suspend")
    public ApiResponse<SalonResponse> suspend(
            @PathVariable Long salonId,
            @Valid @RequestBody(required = false) SalonStatusChangeRequest request
    ) {
        return ApiResponse.ok("Salon suspended", adminSalonService.suspend(salonId, reason(request)));
    }

    @PatchMapping("/{salonId}/activate")
    public ApiResponse<SalonResponse> activate(@PathVariable Long salonId) {
        return ApiResponse.ok("Salon activated", adminSalonService.activate(salonId));
    }

    private String reason(SalonStatusChangeRequest request) {
        return request == null ? null : request.reason();
    }
}
