package com.chatApp.backend.ChatAppBackend.service;

import com.chatApp.backend.ChatAppBackend.dtos.LoginUserDto;
import com.chatApp.backend.ChatAppBackend.dtos.RegisterUserDto;
import com.chatApp.backend.ChatAppBackend.exception.EmailAlreadyExistsException;
import com.chatApp.backend.ChatAppBackend.exception.UserCreationFailureException;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public String signup(RegisterUserDto input) {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        try {
            User user = new User()
                    .setPassword(passwordEncoder.encode(input.getPassword()))
                    .setEmail(input.getEmail())
                    .setFullName(input.getFullName());
            userRepository.save(user);

            return jwtService.generateToken(user);
        } catch (RuntimeException e) {
            throw new UserCreationFailureException("Error Creating new User");
        }
    }

    public String authenticate(LoginUserDto loginUserDto) {
        Authentication authenticatedUser = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );
        if (authenticatedUser.isAuthenticated()) {
            User authUser =  userRepository.findByEmail(loginUserDto.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return jwtService.generateToken(authUser);
        }
        throw new BadCredentialsException("Authentication failed");
    }



}
