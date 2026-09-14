package com.schedora.config;

import com.schedora.availability.entity.WorkingHour;
import com.schedora.availability.repository.WorkingHourRepository;
import com.schedora.branch.entity.Branch;
import com.schedora.branch.repository.BranchRepository;
import com.schedora.offering.entity.ServiceOffering;
import com.schedora.offering.repository.ServiceOfferingRepository;
import com.schedora.salon.entity.Salon;
import com.schedora.salon.entity.SalonStatus;
import com.schedora.salon.repository.SalonRepository;
import com.schedora.staff.entity.Staff;
import com.schedora.staff.repository.StaffRepository;
import com.schedora.user.entity.AppUser;
import com.schedora.user.entity.Role;
import com.schedora.user.entity.UserStatus;
import com.schedora.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               SalonRepository salonRepository,
                               BranchRepository branchRepository,
                               ServiceOfferingRepository offeringRepository,
                               StaffRepository staffRepository,
                               WorkingHourRepository workingHourRepository,
                               PasswordEncoder passwordEncoder,
                               @Value("${schedora.seed.enabled:true}") boolean seedEnabled) {
        return args -> {
            if (!seedEnabled || userRepository.existsByEmail("admin@schedora.local")) {
                return;
            }

            AppUser admin = userRepository.save(new AppUser(
                    "Schedora Super Admin",
                    "admin@schedora.local",
                    "+910000000001",
                    passwordEncoder.encode("Admin@12345"),
                    Role.SUPER_ADMIN,
                    UserStatus.ACTIVE
            ));

            createSalonFixture(
                    userRepository,
                    salonRepository,
                    branchRepository,
                    offeringRepository,
                    staffRepository,
                    workingHourRepository,
                    passwordEncoder,
                    admin,
                    "pending.owner@schedora.local",
                    "Pending Partner",
                    "Glow Studio",
                    SalonStatus.PENDING,
                    null
            );
            createSalonFixture(
                    userRepository,
                    salonRepository,
                    branchRepository,
                    offeringRepository,
                    staffRepository,
                    workingHourRepository,
                    passwordEncoder,
                    admin,
                    "active.owner@schedora.local",
                    "Active Partner",
                    "Urban Cuts",
                    SalonStatus.ACTIVE,
                    null
            );
            createSalonFixture(
                    userRepository,
                    salonRepository,
                    branchRepository,
                    offeringRepository,
                    staffRepository,
                    workingHourRepository,
                    passwordEncoder,
                    admin,
                    "rejected.owner@schedora.local",
                    "Rejected Partner",
                    "Incomplete Salon",
                    SalonStatus.REJECTED,
                    "Documents did not match business details"
            );
            createSalonFixture(
                    userRepository,
                    salonRepository,
                    branchRepository,
                    offeringRepository,
                    staffRepository,
                    workingHourRepository,
                    passwordEncoder,
                    admin,
                    "suspended.owner@schedora.local",
                    "Suspended Partner",
                    "Pause Spa",
                    SalonStatus.SUSPENDED,
                    "Temporarily suspended for policy review"
            );
        };
    }

    private void createSalonFixture(UserRepository userRepository,
                                    SalonRepository salonRepository,
                                    BranchRepository branchRepository,
                                    ServiceOfferingRepository offeringRepository,
                                    StaffRepository staffRepository,
                                    WorkingHourRepository workingHourRepository,
                                    PasswordEncoder passwordEncoder,
                                    AppUser admin,
                                    String ownerEmail,
                                    String ownerName,
                                    String salonName,
                                    SalonStatus status,
                                    String reason) {
        AppUser owner = userRepository.save(new AppUser(
                ownerName,
                ownerEmail,
                "+910000000002",
                passwordEncoder.encode("Owner@12345"),
                Role.SALON_OWNER,
                UserStatus.ACTIVE
        ));

        Salon salon = new Salon(
                owner,
                salonName,
                "Seed salon used for development API testing",
                "+910000000010",
                ownerEmail.replace("owner", "salon"),
                "MG Road",
                "Bengaluru",
                "Karnataka",
                "560001",
                new BigDecimal("12.9716000"),
                new BigDecimal("77.5946000")
        );
        salon.setStatus(status);
        if (status == SalonStatus.ACTIVE) {
            salon.setApprovedAt(LocalDateTime.now());
            salon.setApprovedBy(admin);
        } else if (status == SalonStatus.REJECTED) {
            salon.setRejectedAt(LocalDateTime.now());
            salon.setRejectedBy(admin);
            salon.setRejectionReason(reason);
        } else if (status == SalonStatus.SUSPENDED) {
            salon.setApprovedAt(LocalDateTime.now().minusDays(5));
            salon.setApprovedBy(admin);
            salon.setSuspendedAt(LocalDateTime.now());
            salon.setSuspensionReason(reason);
        }
        salonRepository.save(salon);

        Branch branch = branchRepository.save(new Branch(
                salon,
                salonName + " Main Branch",
                "+910000000011",
                "MG Road",
                "Bengaluru",
                "Karnataka",
                "560001",
                new BigDecimal("12.9716000"),
                new BigDecimal("77.5946000")
        ));

        ServiceOffering haircut = offeringRepository.save(new ServiceOffering(
                salon,
                branch,
                "Haircut",
                "Classic haircut and styling",
                new BigDecimal("350.00"),
                30
        ));
        ServiceOffering facial = offeringRepository.save(new ServiceOffering(
                salon,
                branch,
                "Facial",
                "Skin care facial service",
                new BigDecimal("900.00"),
                60
        ));

        Staff stylist = new Staff(salon, branch, "Raj Kumar", "+910000000021",
                "raj." + ownerEmail, "Senior Stylist");
        stylist.setServices(new LinkedHashSet<>(List.of(haircut, facial)));
        staffRepository.save(stylist);

        for (DayOfWeek day : DayOfWeek.values()) {
            boolean closed = day == DayOfWeek.SUNDAY;
            workingHourRepository.save(new WorkingHour(
                    salon,
                    branch,
                    day,
                    closed ? null : LocalTime.of(10, 0),
                    closed ? null : LocalTime.of(20, 0),
                    closed
            ));
        }
    }
}
