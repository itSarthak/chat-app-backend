package com.chatApp.backend.ChatAppBackend.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    private String _id;

    private String email;

    private String fullName;

    private String profilePic;

    private Date createdAt;

    private String chattyUsername;

}
