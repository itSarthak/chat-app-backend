package com.chatApp.backend.ChatAppBackend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.util.Date;

@Data
public class ReceiveMessageDto {
    @NotNull
    private String text;

    private String image;

    @NotNull
    private String senderId;

    @NotNull
    private String receiverId;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
