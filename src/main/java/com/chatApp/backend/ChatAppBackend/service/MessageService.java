package com.chatApp.backend.ChatAppBackend.service;

import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
import com.chatApp.backend.ChatAppBackend.models.Message;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.repository.MessageRepository;
import com.chatApp.backend.ChatAppBackend.repository.UserRepository;
import com.chatApp.backend.ChatAppBackend.utils.UserMapper;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final MessageRepository messageRepository;

    public MessageService(
            UserRepository userRepository,
            UserMapper userMapper,
            MessageRepository messageRepository
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.messageRepository = messageRepository;
    }

    public List<UserDto> fetchFriendList(String userEmail) {
        List<User> totalUsers = userRepository.findAll();
        return totalUsers.stream()
                .filter(user -> !user.getEmail().equalsIgnoreCase(userEmail))
                .map(userMapper::toUserDto)
                .toList();
    }

    public List<Message> fetchMessages(String senderId, String receiverId) {
        List<Message> messagesSenderSide = messageRepository.findMessageByID(senderId, receiverId);
        List<Message> messagesReceiverSide = messageRepository.findMessageByID(receiverId, senderId);
        List<Message> messageHistory = new ArrayList<>(messagesSenderSide);
        messageHistory.addAll(messagesReceiverSide);
        return messageHistory;
    }
}
