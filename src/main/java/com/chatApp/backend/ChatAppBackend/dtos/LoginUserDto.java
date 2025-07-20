package com.chatApp.backend.ChatAppBackend.dtos;

import lombok.Data;

@Data
public class LoginUserDto {

    private String email;

    private String password;
}
