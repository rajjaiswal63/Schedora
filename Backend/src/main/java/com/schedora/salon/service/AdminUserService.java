package com.schedora.salon.service;

import com.schedora.common.exception.BadRequestException;
import com.schedora.common.exception.ResourceNotFoundException;
import com.schedora.user.entity.AppUser;
import com.schedora.user.entity.Role;
import com.schedora.user.dto.UserResponse;
import com.schedora.user.entity.UserStatus;
import com.schedora.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> salonOwners() {
        return userRepository.findByRoleOrderByCreatedAtDesc(Role.SALON_OWNER).stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse updateSalonOwnerStatus(Long userId, UserStatus status) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.SALON_OWNER) {
            throw new BadRequestException("Only salon-owner accounts can be managed here");
        }
        user.setStatus(status);
        return UserResponse.from(user);
    }
}
