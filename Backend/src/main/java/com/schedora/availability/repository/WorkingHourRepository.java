package com.schedora.availability.repository;

import com.schedora.availability.entity.WorkingHour;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingHourRepository extends JpaRepository<WorkingHour, Long> {
    List<WorkingHour> findByBranchIdAndSalonIdOrderByDayOfWeekAsc(Long branchId, Long salonId);

    Optional<WorkingHour> findByBranchIdAndSalonIdAndDayOfWeek(Long branchId, Long salonId, DayOfWeek dayOfWeek);
}
