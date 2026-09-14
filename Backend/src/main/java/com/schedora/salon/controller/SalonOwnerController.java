package com.schedora.salon.controller;

import com.schedora.common.response.ApiResponse;
import com.schedora.salon.dto.SalonResponse;
import com.schedora.salon.dto.UpdateSalonRequest;
import com.schedora.salon.service.SalonOwnerService;
import com.schedora.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salon-owner")
public class SalonOwnerController {

    private final SalonOwnerService salonOwnerService;

    public SalonOwnerController(SalonOwnerService salonOwnerService) {
        this.salonOwnerService = salonOwnerService;
    }

    @GetMapping("/profile")
    public ApiResponse<UserResponse> profile() {
        return ApiResponse.ok("Profile loaded", salonOwnerService.profile());
    }

    @GetMapping("/salon")
    public ApiResponse<SalonResponse> ownSalon() {
        return ApiResponse.ok("Salon loaded", salonOwnerService.ownSalon());
    }

    @PutMapping("/salon")
    public ApiResponse<SalonResponse> updateOwnSalon(@Valid @RequestBody UpdateSalonRequest request) {
        return ApiResponse.ok("Salon updated", salonOwnerService.updateOwnSalon(request));
    }
}
