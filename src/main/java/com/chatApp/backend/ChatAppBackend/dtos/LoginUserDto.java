package com.chatApp.backend.ChatAppBackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUserDto {

    @NotBlank
    @Email(message="The email address is invalid.")
    private String email;

    private String password;
}
