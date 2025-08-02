package com.chatApp.backend.ChatAppBackend.service;

import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
import com.chatApp.backend.ChatAppBackend.dtos.LoginUserDto;
import com.chatApp.backend.ChatAppBackend.dtos.RegisterUserDto;
import com.chatApp.backend.ChatAppBackend.exception.EmailAlreadyExistsException;
import com.chatApp.backend.ChatAppBackend.exception.UserCreationFailureException;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.repository.UserRepository;
import com.chatApp.backend.ChatAppBackend.utils.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    public Map<String, Object> signup(RegisterUserDto input) {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        try {
            User user = new User()
                    .setPassword(passwordEncoder.encode(input.getPassword()))
                    .setEmail(input.getEmail())
                    .setFullName(input.getFullName());
            userRepository.save(user);
            UserDto userDto = userMapper.toUserDto(
                    userRepository.findByEmail(input.getEmail()).orElseThrow(
                            () -> new UsernameNotFoundException("User not found"))
            );
            Map<String, Object> signupPayload= new HashMap<>();
            signupPayload.put("jwt", jwtService.generateToken(user));
            signupPayload.put("user", userDto);
            return signupPayload;
        } catch (RuntimeException e) {
            throw new UserCreationFailureException("Error Creating new User");
        }
    }

    public Map<String, Object> authenticate(LoginUserDto loginUserDto) {
        Authentication authenticatedUser = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );
        if (authenticatedUser.isAuthenticated()) {
            User authUser =  userRepository.findByEmail(loginUserDto.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found at auth level"));
            UserDto userDto = userMapper.toUserDto(authUser);
            Map<String, Object> loginPayload= new HashMap<>();
            loginPayload.put("jwt", jwtService.generateToken(authUser));
            loginPayload.put("user", userDto);
            return loginPayload;
        }
        throw new BadCredentialsException("Authentication failed");
    }

    public UserDto isUserAuthenticated(String userEmail) {
        UserDto userDto = userMapper.toUserDto(
                userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User does not exists"))
        );
        return userDto;
    }
}
