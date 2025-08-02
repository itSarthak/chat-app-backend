package com.chatApp.backend.ChatAppBackend.controller;

import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
import com.chatApp.backend.ChatAppBackend.models.Message;
import com.chatApp.backend.ChatAppBackend.service.MessageService;
import com.chatApp.backend.ChatAppBackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final UserService userService;


    private final MessageService messageService;

    public MessageController(
            UserService userService,
            MessageService messageService
    ) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsersForSidebar(HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        List<UserDto> listOfFriends = messageService.fetchFriendList(userEmail);
        return ResponseEntity.ok().body(listOfFriends);
    }

    @GetMapping("/{receiver_id}")
    public ResponseEntity<List<Message>> getUserMessages(@PathVariable String receiver_id, HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        String sender_id = userService.fetchUserIdFromMail(userEmail);
        List<Message> chatHistory = messageService.fetchMessages(sender_id, receiver_id);
        return ResponseEntity.ok().body(chatHistory);
    }


}
