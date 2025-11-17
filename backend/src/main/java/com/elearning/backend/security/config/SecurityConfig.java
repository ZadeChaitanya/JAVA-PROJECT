package com.elearning.backend.security.config;

import com.elearning.backend.security.filter.JwtAuthFilter;
import com.elearning.backend.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Optional, but good practice
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables method-level security (@PreAuthorize)
@RequiredArgsConstructor // Automatically injects final fields via constructor
public class SecurityConfig {

    // Injected Dependencies (MUST be declared as final)
    private final JwtAuthFilter authFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    // -------------------------------------------------------------
    // 1. FILTER CHAIN (Defines URL Access Rules and JWT Filter Order)
    // -------------------------------------------------------------

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public/Onboarding Endpoints: Permitted without any token
                        .requestMatchers("/api/students", "/api/teachers", "/api/auth/login", "/api/hello").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/results").hasAuthority("ROLE_STUDENT")
                        .requestMatchers("/ws/**").permitAll()

                        // All other API endpoints REQUIRE a valid token
                        .anyRequest().authenticated()
                )
                // Set session management to stateless (required for JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Integrate the custom provider (user lookup/password verification)
                .authenticationProvider(authenticationProvider())

                // Add the JWT filter to run BEFORE Spring's default authentication filter
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // -------------------------------------------------------------
    // 2. AUTHENTICATION CORE BEANS
    // -------------------------------------------------------------

    // Defines how to authenticate (UserDetailsService + PasswordEncoder)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // This relies on UserDetailsServiceImpl being injected into the SecurityConfig constructor
        authenticationProvider.setUserDetailsService(userDetailsServiceImpl);

        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // Exposes the AuthenticationManager bean for AuthController to inject
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Defines the PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//    @Bean
//    static MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
//        return new DefaultMethodSecurityExpressionHandler();
//    }
    @Bean
    public static MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setDefaultRolePrefix("");

        return expressionHandler;
    }
}