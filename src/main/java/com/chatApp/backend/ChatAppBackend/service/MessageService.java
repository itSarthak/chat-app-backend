package com.chatApp.backend.ChatAppBackend.service;

import com.chatApp.backend.ChatAppBackend.dtos.MessageDto;
import com.chatApp.backend.ChatAppBackend.dtos.ReceiveMessageDto;
import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
import com.chatApp.backend.ChatAppBackend.models.Message;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.repository.MessageRepository;
import com.chatApp.backend.ChatAppBackend.repository.UserRepository;
import com.chatApp.backend.ChatAppBackend.service.socket.OnlineUserManager;
import com.chatApp.backend.ChatAppBackend.utils.DateParser;
import com.chatApp.backend.ChatAppBackend.utils.MessageMapper;
import com.chatApp.backend.ChatAppBackend.utils.UserMapper;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.*;

@Service
public class MessageService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final MessageRepository messageRepository;

    private final CloudinaryService cloudinaryService;

    private final OnlineUserManager onlineUserManager;

    private final SocketIOServer socketIOServer;

    private final MessageMapper messageMapper;

    private final DateParser dateParser;

    public MessageService(
            UserRepository userRepository,
            UserMapper userMapper,
            MessageRepository messageRepository,
            CloudinaryService cloudinaryService,
            OnlineUserManager onlineUserManager,
            SocketIOServer socketIOServer,
            MessageMapper messageMapper,
            DateParser dateParser
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.messageRepository = messageRepository;
        this.cloudinaryService = cloudinaryService;
        this.onlineUserManager = onlineUserManager;
        this.socketIOServer = socketIOServer;
        this.messageMapper = messageMapper;
        this.dateParser = dateParser;
    }

    public List<UserDto> fetchFriendList(String userEmail) {
        List<User> totalUsers = userRepository.findAll();
        return totalUsers.stream()
                .filter(user -> !user.getEmail().equalsIgnoreCase(userEmail))
                .map(userMapper::toUserDto)
                .toList();
    }

    public List<ReceiveMessageDto> fetchMessages(String senderId, String receiverId) {
        List<Message> messageHistory = messageRepository.findMessagesBetweenUsers(senderId, receiverId);
        List<ReceiveMessageDto> messagesDtoHistory= new ArrayList<>();
        for (Message message : messageHistory) {
            messagesDtoHistory.add(messageMapper.toReceiveMessageDto(message));
        }
        messagesDtoHistory.sort(Comparator.comparing(ReceiveMessageDto::getCreatedAt));
        return messagesDtoHistory;
    }

    public List<ReceiveMessageDto> fetchMessagesPaginated(String senderId, String receiverId, String createdAt) {
        Pageable pageable = PageRequest.of(
                0,
                15,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Date createdAtFormatted = dateParser.parseStringToDate(createdAt);
        List<Message> messages = messageRepository.findMessagesBeforeDate(senderId, receiverId, createdAtFormatted, pageable);
        return messages.stream()
                .map(messageMapper::toReceiveMessageDto)
                .toList().reversed();
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
            socketIOServer.getClient(UUID.fromString(receiverSocketId)).sendEvent("newMessage", messageMapper.toReceiveMessageDto(newMessage));
        }
        return newMessage;
    }
}
