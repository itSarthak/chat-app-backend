package com.chatApp.backend.ChatAppBackend.controller;

import com.chatApp.backend.ChatAppBackend.dtos.ImageUpdateDto;
import com.chatApp.backend.ChatAppBackend.dtos.LoginUserDto;
import com.chatApp.backend.ChatAppBackend.dtos.RegisterUserDto;
import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/auth")
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
         Map<String, Object> res = authService.signup(registerUserDto);
        ResponseCookie cookie = ResponseCookie.from("jwt", res.get("jwt").toString())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(SameSiteCookies.STRICT.toString())
                .maxAge(3600)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res.get("user"));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        Map<String, Object> res = authService.authenticate(loginUserDto);

        ResponseCookie cookie = ResponseCookie.from("jwt", res.get("jwt").toString())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(3600)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res.get("user"));
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
    @GetMapping("/check")
    public ResponseEntity<UserDto> checkIfAuthenticated(HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        UserDto userDto = authService.isUserAuthenticated(userEmail);
        return ResponseEntity.ok().body(userDto);
    }
}
