package com.schedora.branch.repository;

import com.schedora.branch.entity.Branch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findBySalonIdOrderByCreatedAtDesc(Long salonId);
}
