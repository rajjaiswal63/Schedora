package com.schedora.availability.repository;

import com.schedora.availability.entity.StaffAvailability;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffAvailabilityRepository extends JpaRepository<StaffAvailability, Long> {
    List<StaffAvailability> findByStaffIdAndSalonIdOrderByDayOfWeekAsc(Long staffId, Long salonId);

    Optional<StaffAvailability> findByStaffIdAndSalonIdAndDayOfWeek(Long staffId, Long salonId, DayOfWeek dayOfWeek);
}
