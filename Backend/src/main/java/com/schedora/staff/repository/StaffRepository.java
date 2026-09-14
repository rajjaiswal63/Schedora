package com.schedora.staff.repository;

import com.schedora.staff.entity.Staff;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    @EntityGraph(attributePaths = {"branch", "services"})
    List<Staff> findBySalonIdOrderByCreatedAtDesc(Long salonId);
}
