package com.elearning.backend.controller;

import com.elearning.backend.dto.AuthRequest;
import com.elearning.backend.dto.AuthResponse;
import com.elearning.backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {

        // 1. Attempt to authenticate the user
        // This process uses the UserDetailsServiceImpl and PasswordEncoder to verify credentials.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {

            // 2. Authentication successful: Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails.getUsername());

            // 3. Extract Role and ID for the response DTO
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            // NOTE: Since Student and Teacher implement UserDetails, we need to cast
            // them back to get their specific ID. This is a common pattern.
            Long userId = null;
            if(userDetails instanceof com.elearning.backend.model.Admin admin) { // <-- NEW CHECK
                userId = admin.getId();
            } else if (userDetails instanceof com.elearning.backend.model.Teacher) {
                userId = ((com.elearning.backend.model.Teacher) userDetails).getId();
            } else if (userDetails instanceof com.elearning.backend.model.Student) {
                userId = ((com.elearning.backend.model.Student) userDetails).getId();
            }
            // 4. Assemble and return the success response (200 OK)
            AuthResponse response = new AuthResponse(token, role, userId);
            return ResponseEntity.ok(response);

        } else {
            // 5. Authentication failed (This line may be hit only if the security chain fails earlier)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}