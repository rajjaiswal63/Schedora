package com.schedora.auth.controller;

import com.schedora.auth.dto.AuthResponse;
import com.schedora.auth.dto.LoginRequest;
import com.schedora.auth.dto.SalonOwnerRegistrationRequest;
import com.schedora.auth.service.AuthenticationService;
import com.schedora.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/salon-owner/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerSalonOwner(
            @Valid @RequestBody SalonOwnerRegistrationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Salon owner registered and salon application submitted",
                        authenticationService.registerSalonOwner(request)));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login successful", authenticationService.login(request));
    }
}
