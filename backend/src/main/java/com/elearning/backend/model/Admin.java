package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Data; // Provides basic getters and setters
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "admins") // New table for admin accounts
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Authentication Fields ---
    @Column(unique = true, nullable = false)
    private String email; // Used as the unique username for login

    @JsonIgnore // Hides the password hash in API responses
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Allows reading from POST requests
    private String password;
    // -----------------------------

    private String name;
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;

    // --- UserDetails Implementation ---

    // 1. Get Authorities (Roles)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Assigns the specific role for Admin access control
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    // 2. Get Password (Used by the PasswordEncoder)
    @Override
    public String getPassword() {
        return this.password;
    }

    // 3. Get Username (Used for AuthenticationManager lookup)
    @Override
    public String getUsername() {
        return this.email;
    }

    // 4. Account Status Methods (Required by Spring Security)
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}