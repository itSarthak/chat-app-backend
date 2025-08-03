package com.chatApp.backend.ChatAppBackend.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.*;

@Data
@Document(collection = "messages")
@Accessors(chain = true)
public class Message {
    @Id
    private String id;

    @NotNull
    @DBRef
    private User sender;

    @NotNull
    @DBRef
    private User receiver;

    private String text;

    private String image;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
