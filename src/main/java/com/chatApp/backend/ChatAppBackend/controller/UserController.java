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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/profile")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update/avatar")
    public ResponseEntity<UserDto> updateProfile(HttpServletRequest request, @ModelAttribute ImageUpdateDto image) {
        String userEmail = request.getUserPrincipal().getName();
        UserDto userDto = userService.updateProfile(userEmail, image);
        return ResponseEntity.status(201).body(userDto);
    }

    @PutMapping("/update/data")
    public ResponseEntity<UserDto> updateData(HttpServletRequest request, @RequestBody Map<String, Object> extraData) {
        String userEmail = request.getUserPrincipal().getName();
        UserDto userDto = userService.updateData(userEmail, extraData);
        return ResponseEntity.status(201).body(userDto);
    }


}
