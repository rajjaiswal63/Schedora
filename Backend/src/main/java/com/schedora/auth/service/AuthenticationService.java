package com.schedora.auth.service;

import com.schedora.auth.dto.AuthResponse;
import com.schedora.auth.dto.LoginRequest;
import com.schedora.auth.dto.SalonOwnerRegistrationRequest;
import com.schedora.auth.security.JwtService;
import com.schedora.common.exception.DuplicateResourceException;
import com.schedora.salon.entity.Salon;
import com.schedora.salon.repository.SalonRepository;
import com.schedora.user.entity.AppUser;
import com.schedora.user.entity.Role;
import com.schedora.user.entity.UserStatus;
import com.schedora.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SalonRepository salonRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository,
                                 SalonRepository salonRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 JwtService jwtService) {
        this.userRepository = userRepository;
        this.salonRepository = salonRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registerSalonOwner(SalonOwnerRegistrationRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        AppUser owner = new AppUser(
                request.ownerName().trim(),
                normalizedEmail,
                request.phone().trim(),
                passwordEncoder.encode(request.password()),
                Role.SALON_OWNER,
                UserStatus.ACTIVE
        );
        userRepository.save(owner);

        Salon salon = new Salon(
                owner,
                request.salonName().trim(),
                request.salonDescription(),
                request.salonPhone().trim(),
                request.salonEmail().trim().toLowerCase(),
                request.addressLine().trim(),
                request.city().trim(),
                request.state().trim(),
                request.pincode().trim(),
                request.latitude(),
                request.longitude()
        );
        salonRepository.save(salon);

        return toAuthResponse(owner, salon.getStatus().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
        );
        AppUser user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user missing"));
        String salonStatus = salonRepository.findByOwnerId(user.getId())
                .map(salon -> salon.getStatus().name())
                .orElse(null);
        return toAuthResponse(user, salonStatus);
    }

    private AuthResponse toAuthResponse(AppUser user, String salonStatus) {
        String token = jwtService.generateToken(user, user.getId());
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                salonStatus
        );
    }
}
