package com.chatApp.backend.ChatAppBackend.controller;

import com.chatApp.backend.ChatAppBackend.dtos.ImageUpdateDto;
import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
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
@RequestMapping("/api")
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;

    public UserController(JwtService jwtService, UserService userService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PutMapping("/profile-update")
    public ResponseEntity<UserDto> updateProfile(HttpServletRequest request, @ModelAttribute ImageUpdateDto image) {
        final String authHeader = request.getHeader("Authorization");
        String userEmail = request.getUserPrincipal().getName();
        UserDto userDto = userService.updateProfile(userEmail, image);
        return ResponseEntity.status(201).body(userDto);
    }

}
