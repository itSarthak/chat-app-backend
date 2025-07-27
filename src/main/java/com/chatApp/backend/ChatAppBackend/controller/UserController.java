package com.chatApp.backend.ChatAppBackend.controller;

import com.chatApp.backend.ChatAppBackend.dtos.ImageUpdateDto;
import com.chatApp.backend.ChatAppBackend.service.JwtService;
import com.chatApp.backend.ChatAppBackend.service.UserService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;

    public UserController(JwtService jwtService, UserService userService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PutMapping("/profile-update")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @ModelAttribute ImageUpdateDto image) {
        final String authHeader = request.getHeader("Authorization");
        String userEmail = request.getUserPrincipal().getName();
        String uploadResponse = userService.updateProfile(userEmail, image);
        return ResponseEntity.status(201).body(Map.of("message", uploadResponse));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkIfAuthenticated() {
        return ResponseEntity.ok("User Authenticated");
    }
}
