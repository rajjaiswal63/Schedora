package com.schedora.offering.repository;

import com.schedora.offering.entity.ServiceOffering;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    List<ServiceOffering> findBySalonIdOrderByCreatedAtDesc(Long salonId);

    List<ServiceOffering> findByIdInAndSalonId(Collection<Long> ids, Long salonId);
}
