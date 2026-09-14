package com.schedora.auth.dto;

import com.schedora.user.entity.Role;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String name,
        String email,
        Role role,
        String salonStatus
) {
}
