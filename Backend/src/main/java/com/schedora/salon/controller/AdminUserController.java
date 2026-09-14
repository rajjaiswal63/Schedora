package com.schedora.salon.controller;

import com.schedora.common.response.ApiResponse;
import com.schedora.salon.service.AdminUserService;
import com.schedora.user.entity.UserStatus;
import com.schedora.user.dto.UserResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/salon-owners")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> salonOwners() {
        return ApiResponse.ok("Salon owners loaded", adminUserService.salonOwners());
    }

    @PatchMapping("/{userId}/activate")
    public ApiResponse<UserResponse> activate(@PathVariable Long userId) {
        return ApiResponse.ok("Salon owner activated",
                adminUserService.updateSalonOwnerStatus(userId, UserStatus.ACTIVE));
    }

    @PatchMapping("/{userId}/deactivate")
    public ApiResponse<UserResponse> deactivate(@PathVariable Long userId) {
        return ApiResponse.ok("Salon owner deactivated",
                adminUserService.updateSalonOwnerStatus(userId, UserStatus.INACTIVE));
    }

    @PatchMapping("/{userId}/block")
    public ApiResponse<UserResponse> block(@PathVariable Long userId) {
        return ApiResponse.ok("Salon owner blocked",
                adminUserService.updateSalonOwnerStatus(userId, UserStatus.BLOCKED));
    }
}
