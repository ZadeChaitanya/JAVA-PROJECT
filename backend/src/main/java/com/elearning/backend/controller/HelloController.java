package com.elearning.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    public class HelloController {
        @GetMapping("/hello")
        @CrossOrigin(origins = "*") // allow frontend later
        public String home() {
            return "Spring Boot backend is running successfully!";
        }
    }


