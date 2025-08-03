package com.chatApp.backend.ChatAppBackend.service;

import com.chatApp.backend.ChatAppBackend.dtos.MessageDto;
import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
import com.chatApp.backend.ChatAppBackend.models.Message;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.repository.MessageRepository;
import com.chatApp.backend.ChatAppBackend.repository.UserRepository;
import com.chatApp.backend.ChatAppBackend.service.socket.OnlineUserManager;
import com.chatApp.backend.ChatAppBackend.utils.UserMapper;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final MessageRepository messageRepository;

    private final CloudinaryService cloudinaryService;

    private final OnlineUserManager onlineUserManager;

    private final SocketIOServer socketIOServer;

    public MessageService(
            UserRepository userRepository,
            UserMapper userMapper,
            MessageRepository messageRepository,
            CloudinaryService cloudinaryService,
            OnlineUserManager onlineUserManager,
            SocketIOServer socketIOServer
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.messageRepository = messageRepository;
        this.cloudinaryService = cloudinaryService;
        this.onlineUserManager = onlineUserManager;
        this.socketIOServer = socketIOServer;
    }

    public List<UserDto> fetchFriendList(String userEmail) {
        List<User> totalUsers = userRepository.findAll();
        return totalUsers.stream()
                .filter(user -> !user.getEmail().equalsIgnoreCase(userEmail))
                .map(userMapper::toUserDto)
                .toList();
    }

    public List<Message> fetchMessages(String senderId, String receiverId) {
        List<Message> messageHistory = messageRepository.findMessagesBetweenUsers(senderId, receiverId);
        messageHistory.sort(Comparator.comparing(Message::getCreatedAt));
        //        List<Message> messageHistory = messageRepository.();
        return messageHistory;
    }

    public Message sendMessage(String senderId, String receiverId, MessageDto messageDto) {
        Message newMessage = new Message();
        User sender = userRepository.findById(senderId).orElseThrow(() -> new UsernameNotFoundException("This User doesn't exist."));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new UsernameNotFoundException("This User doesn't exist."));
        if (messageDto.getImage() != null) {
            String uploadedImageUri = cloudinaryService.uploadFile(messageDto.getImage(), "chatApp_sharedImage");
            newMessage.setImage(uploadedImageUri);
        }
        newMessage.setText(messageDto.getText());
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        messageRepository.save(newMessage);
        String receiverSocketId = onlineUserManager.getSocketIdByUserId(receiverId);
        if (receiverSocketId != null) {
            socketIOServer.getClient(UUID.fromString(receiverSocketId)).sendEvent("newMessage", newMessage);
        }
        return newMessage;
    }
}
