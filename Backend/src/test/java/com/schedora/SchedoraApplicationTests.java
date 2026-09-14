package com.schedora;

import com.jayway.jsonpath.JsonPath;
import com.schedora.availability.repository.StaffAvailabilityRepository;
import com.schedora.availability.repository.WorkingHourRepository;
import com.schedora.branch.repository.BranchRepository;
import com.schedora.offering.repository.ServiceOfferingRepository;
import com.schedora.salon.repository.SalonRepository;
import com.schedora.staff.repository.StaffRepository;
import com.schedora.user.entity.AppUser;
import com.schedora.user.entity.Role;
import com.schedora.user.entity.UserStatus;
import com.schedora.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "schedora.seed.enabled=false",
        "schedora.jwt.secret=test-only-schedora-jwt-secret-key-with-enough-length-2026",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class SchedoraApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalonRepository salonRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ServiceOfferingRepository offeringRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private WorkingHourRepository workingHourRepository;

    @Autowired
    private StaffAvailabilityRepository staffAvailabilityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void resetData() {
        staffAvailabilityRepository.deleteAll();
        workingHourRepository.deleteAll();
        staffRepository.deleteAll();
        offeringRepository.deleteAll();
        branchRepository.deleteAll();
        salonRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new AppUser(
                "Test Super Admin",
                "admin@test.local",
                "+910000000001",
                passwordEncoder.encode("Admin@12345"),
                Role.SUPER_ADMIN,
                UserStatus.ACTIVE
        ));
    }

    @Test
    void registrationLoginAndPasswordHashingWork() throws Exception {
        RegistrationResult owner = registerOwner("owner-auth@test.local", "Auth Salon");

        assertThat(owner.token()).isNotBlank();
        assertThat(owner.salonStatus()).isEqualTo("PENDING");

        AppUser savedOwner = userRepository.findByEmail("owner-auth@test.local").orElseThrow();
        assertThat(savedOwner.getPassword()).isNotEqualTo("Owner@12345");
        assertThat(passwordEncoder.matches("Owner@12345", savedOwner.getPassword())).isTrue();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", "owner-auth@test.local",
                                "password", "Owner@12345"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("SALON_OWNER"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", "owner-auth@test.local",
                                "password", "wrong-password"
                        ))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", "missing@test.local",
                                "password", "Owner@12345"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedApisRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/salon-owner/salon"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminWorkflowTransitionsAreEnforced() throws Exception {
        RegistrationResult alpha = registerOwner("alpha@test.local", "Alpha Salon");
        RegistrationResult beta = registerOwner("beta@test.local", "Beta Salon");
        String adminToken = login("admin@test.local", "Admin@12345");

        mockMvc.perform(patch("/api/admin/salons/{salonId}/approve", alpha.salonId())
                        .header("Authorization", bearer(alpha.token())))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/admin/salons/{salonId}/approve", alpha.salonId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.approvedById").isNumber());

        mockMvc.perform(patch("/api/admin/salons/{salonId}/reject", beta.salonId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("reason", "Documents are incomplete"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.rejectionReason").value("Documents are incomplete"));

        mockMvc.perform(patch("/api/admin/salons/{salonId}/suspend", alpha.salonId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("reason", "Policy review"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUSPENDED"));

        mockMvc.perform(patch("/api/admin/salons/{salonId}/activate", alpha.salonId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        mockMvc.perform(patch("/api/admin/salons/{salonId}/reject", alpha.salonId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("INVALID_STATE_TRANSITION"));
    }

    @Test
    void salonOwnersCannotAccessAnotherOwnersResources() throws Exception {
        RegistrationResult alpha = registerOwner("alpha-resources@test.local", "Alpha Resource Salon");
        RegistrationResult beta = registerOwner("beta-resources@test.local", "Beta Resource Salon");

        Long alphaBranchId = createBranch(alpha.token(), "Alpha Main");
        Long alphaServiceId = createService(alpha.token(), alphaBranchId, "Haircut", "300.00", 30);
        Long alphaStaffId = createStaff(alpha.token(), alphaBranchId, List.of(alphaServiceId));

        mockMvc.perform(get("/api/salon-owner/branches/{branchId}", alphaBranchId)
                        .header("Authorization", bearer(beta.token())))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/salon-owner/services/{serviceId}", alphaServiceId)
                        .header("Authorization", bearer(beta.token())))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/salon-owner/staff/{staffId}", alphaStaffId)
                        .header("Authorization", bearer(beta.token())))
                .andExpect(status().isForbidden());
    }

    @Test
    void serviceValidationRejectsInvalidPricing() throws Exception {
        RegistrationResult owner = registerOwner("validation@test.local", "Validation Salon");
        Long branchId = createBranch(owner.token(), "Validation Main");

        mockMvc.perform(post("/api/salon-owner/services")
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "branchId", branchId,
                                "name", "Haircut",
                                "description", "Invalid price test",
                                "price", new BigDecimal("-1.00"),
                                "durationMinutes", 30
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.validationErrors.price").exists());
    }

    @Test
    void workingHoursAndStaffAvailabilityCanBeConfigured() throws Exception {
        RegistrationResult owner = registerOwner("availability@test.local", "Availability Salon");
        Long branchId = createBranch(owner.token(), "Availability Main");
        Long serviceId = createService(owner.token(), branchId, "Facial", "900.00", 60);
        Long staffId = createStaff(owner.token(), branchId, List.of(serviceId));

        mockMvc.perform(put("/api/salon-owner/working-hours")
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "branchId", branchId,
                                "entries", List.of(
                                        Map.of("dayOfWeek", "MONDAY", "opensAt", "10:00:00", "closesAt", "20:00:00", "closed", false),
                                        Map.of("dayOfWeek", "SUNDAY", "closed", true)
                                )
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));

        mockMvc.perform(put("/api/salon-owner/staff/{staffId}/availability", staffId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "entries", List.of(
                                        Map.of("dayOfWeek", "MONDAY", "availableFrom", "10:00:00", "availableTo", "18:00:00", "unavailable", false),
                                        Map.of("dayOfWeek", "SUNDAY", "unavailable", true)
                                )
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    private RegistrationResult registerOwner(String email, String salonName) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ownerName", salonName + " Owner");
        body.put("email", email);
        body.put("phone", "+910000000099");
        body.put("password", "Owner@12345");
        body.put("salonName", salonName);
        body.put("salonDescription", "Salon under test");
        body.put("salonPhone", "+910000000100");
        body.put("salonEmail", email.replace("@", ".salon@"));
        body.put("addressLine", "Test Street");
        body.put("city", "Bengaluru");
        body.put("state", "Karnataka");
        body.put("pincode", "560001");
        body.put("latitude", new BigDecimal("12.9716000"));
        body.put("longitude", new BigDecimal("77.5946000"));

        String registerResponse = mockMvc.perform(post("/api/auth/salon-owner/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.salonStatus").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(registerResponse, "$.data.token");
        String status = JsonPath.read(registerResponse, "$.data.salonStatus");
        Number ownerId = JsonPath.read(registerResponse, "$.data.userId");

        String salonResponse = mockMvc.perform(get("/api/salon-owner/salon")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Number salonId = JsonPath.read(salonResponse, "$.data.id");

        return new RegistrationResult(token, ownerId.longValue(), salonId.longValue(), status);
    }

    private String login(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(response, "$.data.token");
    }

    private Long createBranch(String token, String name) throws Exception {
        String response = mockMvc.perform(post("/api/salon-owner/branches")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", name,
                                "phone", "+910000000111",
                                "addressLine", "Branch Street",
                                "city", "Bengaluru",
                                "state", "Karnataka",
                                "pincode", "560001",
                                "latitude", new BigDecimal("12.9716000"),
                                "longitude", new BigDecimal("77.5946000")
                        ))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Number id = JsonPath.read(response, "$.data.id");
        return id.longValue();
    }

    private Long createService(String token, Long branchId, String name, String price, int durationMinutes) throws Exception {
        String response = mockMvc.perform(post("/api/salon-owner/services")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "branchId", branchId,
                                "name", name,
                                "description", name + " description",
                                "price", new BigDecimal(price),
                                "durationMinutes", durationMinutes
                        ))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Number id = JsonPath.read(response, "$.data.id");
        return id.longValue();
    }

    private Long createStaff(String token, Long branchId, List<Long> serviceIds) throws Exception {
        String response = mockMvc.perform(post("/api/salon-owner/staff")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "branchId", branchId,
                                "name", "Test Stylist",
                                "phone", "+910000000222",
                                "email", "stylist-" + branchId + "@test.local",
                                "designation", "Stylist",
                                "serviceIds", serviceIds
                        ))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Number id = JsonPath.read(response, "$.data.id");
        return id.longValue();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String json(Object value) throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper()
                .findAndRegisterModules()
                .writeValueAsString(value);
    }

    private record RegistrationResult(String token, Long ownerId, Long salonId, String salonStatus) {
    }
}
