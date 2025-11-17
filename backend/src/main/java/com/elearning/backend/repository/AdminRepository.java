package com.elearning.backend.repository;// Example Admin Entity
// public class Admin implements UserDetails { ... getAuthorities() { return List.of(new SimpleGrantedAuthority("ROLE_ADMIN")); } ... }

import com.elearning.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
}