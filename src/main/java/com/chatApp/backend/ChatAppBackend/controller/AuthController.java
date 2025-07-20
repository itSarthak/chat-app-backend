package com.chatApp.backend.ChatAppBackend.controller;

import com.chatApp.backend.ChatAppBackend.dtos.LoginUserDto;
import com.chatApp.backend.ChatAppBackend.dtos.RegisterUserDto;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.service.AuthService;
import com.chatApp.backend.ChatAppBackend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/auth")
@RestController
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        String jwt = authService.signup(registerUserDto);
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(SameSiteCookies.STRICT.toString())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Signup Successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        String jwt = authService.authenticate(loginUserDto);

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(SameSiteCookies.STRICT.toString())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Login Successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // <--- expire it immediately
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .body(Map.of("message", "Logout Successful"));
    }
}
