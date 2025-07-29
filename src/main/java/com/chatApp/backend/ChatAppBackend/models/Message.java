package com.chatApp.backend.ChatAppBackend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;

    @DBRef
    private User senderId;

    @DBRef
    private User receiverId;

    private String text;
    private String image;

    private Instant createdAt;
    private Instant updatedAt;
}
