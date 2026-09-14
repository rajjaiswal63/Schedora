package com.schedora.user.repository;

import com.schedora.user.entity.AppUser;
import com.schedora.user.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<AppUser> findByRoleOrderByCreatedAtDesc(Role role);
}
