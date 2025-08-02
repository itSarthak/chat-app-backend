package com.chatApp.backend.ChatAppBackend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MessageDto {

    @NotNull
    private String text;

    private MultipartFile image;
}
