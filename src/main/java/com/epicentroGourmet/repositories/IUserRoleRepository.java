package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findByRole(UserRole.RolEnum role);
}
