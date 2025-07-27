package com.chatApp.backend.ChatAppBackend.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageUpdateDto {
    private String name;
    private MultipartFile file;
}
