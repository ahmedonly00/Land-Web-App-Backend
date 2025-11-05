package com.iwacu250.landplots.repository;

import com.iwacu250.landplots.entity.ERole;
import com.iwacu250.landplots.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
