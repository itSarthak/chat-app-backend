package com.chatApp.backend.ChatAppBackend.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "requests")
@Accessors(chain = true)
public class FriendRequests {
    @Id
    private String id;

    @NotNull
    @DBRef
    private User requestSender;

    @NotNull
    @DBRef
    private User requestReceiver;

    private Boolean isApproved;

    @CreatedDate
    private Date requestSince;

    @LastModifiedDate
    private Date requestRespondDate;
}
