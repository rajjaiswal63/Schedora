package com.schedora.branch.controller;

import com.schedora.branch.dto.BranchRequest;
import com.schedora.branch.dto.BranchResponse;
import com.schedora.branch.service.BranchService;
import com.schedora.common.response.ApiResponse;
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
@RequestMapping("/api/salon-owner/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public ApiResponse<List<BranchResponse>> listBranches() {
        return ApiResponse.ok("Branches loaded", branchService.listBranches());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Branch created", branchService.createBranch(request)));
    }

    @GetMapping("/{branchId}")
    public ApiResponse<BranchResponse> getBranch(@PathVariable Long branchId) {
        return ApiResponse.ok("Branch loaded", branchService.getBranch(branchId));
    }

    @PutMapping("/{branchId}")
    public ApiResponse<BranchResponse> updateBranch(
            @PathVariable Long branchId,
            @Valid @RequestBody BranchRequest request
    ) {
        return ApiResponse.ok("Branch updated", branchService.updateBranch(branchId, request));
    }

    @DeleteMapping("/{branchId}")
    public ApiResponse<BranchResponse> deactivateBranch(@PathVariable Long branchId) {
        return ApiResponse.ok("Branch deactivated", branchService.deactivateBranch(branchId));
    }
}
