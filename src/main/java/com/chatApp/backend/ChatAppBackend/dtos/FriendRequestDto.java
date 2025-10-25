package com.chatApp.backend.ChatAppBackend.dtos;

import com.chatApp.backend.ChatAppBackend.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Data
public class FriendRequestDto {

    private String id;

    private UserDto requestSender;

    private UserDto requestReceiver;

    private boolean isApproved;

    private Date requestSince;

    private Date requestRespondDate;

}
