package com.schedora.salon.repository;

import com.schedora.salon.entity.Salon;
import com.schedora.salon.entity.SalonStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonRepository extends JpaRepository<Salon, Long> {
    @EntityGraph(attributePaths = {"owner", "approvedBy", "rejectedBy"})
    Optional<Salon> findWithOwnerById(Long id);

    @EntityGraph(attributePaths = {"owner", "approvedBy", "rejectedBy"})
    List<Salon> findByStatusOrderByCreatedAtDesc(SalonStatus status);

    @EntityGraph(attributePaths = {"owner", "approvedBy", "rejectedBy"})
    List<Salon> findAllByOrderByCreatedAtDesc();

    Optional<Salon> findByOwnerId(Long ownerId);
}
