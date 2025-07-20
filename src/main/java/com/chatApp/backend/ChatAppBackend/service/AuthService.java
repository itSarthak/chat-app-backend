package com.chatApp.backend.ChatAppBackend.service;

import com.chatApp.backend.ChatAppBackend.dtos.RegisterUserDto;
import com.chatApp.backend.ChatAppBackend.exception.EmailAlreadyExistsException;
import com.chatApp.backend.ChatAppBackend.exception.UserCreationFailureException;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.repository.UserRepository;
import com.mongodb.MongoWriteException;
import jakarta.validation.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
}
