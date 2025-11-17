package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "teachers")
public class Teacher implements UserDetails {

    // --- Explicit Accessors for basic fields ---
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String subject;
    @Setter
    @Getter
    private String email;

    // --- Critical Password Field ---
    @JsonIgnore
    // This allows Jackson to read the field from the incoming JSON body
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    // -------------------------------

    // --- Constructors ---
    public Teacher() {}
    // ... other constructors if needed ...


    // --- FIX: EXPLICIT PASSWORD ACCESSORS ---

    // The setter used by Jackson for deserializing the incoming JSON
    @JsonSetter
    public void setPassword(String password) {
        this.password = password;
    }

    // The getter used by TeacherService.registerNewTeacher()
    @Override
    public String getPassword() {
        return this.password;
    }

    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_TEACHER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}