package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "students")
public class Student implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int standard;
    private String email;

    // --- Critical Password Field ---
    @JsonIgnore
    // Allows Jackson to read the field from the incoming JSON body
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    // -------------------------------

    // --- Constructors ---
    public Student() {}

    public Student(String name, int standard, String email) {
        this.name = name;
        this.standard = standard;
        this.email = email;
    }

    // --- Explicit Accessors (Necessary to guarantee JSON binding) ---

    public Long getId() { return id; }
    // Setter is not needed for ID as it's auto-generated

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStandard() { return standard; }
    public void setStandard(int standard) { this.standard = standard; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // --- FIX: Explicit Password Getter and Setter ---

    // The setter ensures Jackson can receive the password from the POST body
    @JsonSetter
    public void setPassword(String password) {
        this.password = password;
    }

    // The getter used by the StudentController: rawPassword = student.getPassword()
    @Override
    public String getPassword() {
        return this.password;
    }

    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
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