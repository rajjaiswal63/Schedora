package com.schedora.offering.controller;

import com.schedora.common.response.ApiResponse;
import com.schedora.offering.dto.ServiceOfferingRequest;
import com.schedora.offering.dto.ServiceOfferingResponse;
import com.schedora.offering.service.ServiceOfferingService;
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
@RequestMapping("/api/salon-owner/services")
public class ServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;

    public ServiceOfferingController(ServiceOfferingService serviceOfferingService) {
        this.serviceOfferingService = serviceOfferingService;
    }

    @GetMapping
    public ApiResponse<List<ServiceOfferingResponse>> listServices() {
        return ApiResponse.ok("Services loaded", serviceOfferingService.listServices());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceOfferingResponse>> createService(
            @Valid @RequestBody ServiceOfferingRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Service created", serviceOfferingService.createService(request)));
    }

    @GetMapping("/{serviceId}")
    public ApiResponse<ServiceOfferingResponse> getService(@PathVariable Long serviceId) {
        return ApiResponse.ok("Service loaded", serviceOfferingService.getService(serviceId));
    }

    @PutMapping("/{serviceId}")
    public ApiResponse<ServiceOfferingResponse> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceOfferingRequest request
    ) {
        return ApiResponse.ok("Service updated", serviceOfferingService.updateService(serviceId, request));
    }

    @DeleteMapping("/{serviceId}")
    public ApiResponse<ServiceOfferingResponse> deactivateService(@PathVariable Long serviceId) {
        return ApiResponse.ok("Service deactivated", serviceOfferingService.deactivateService(serviceId));
    }
}
