package com.chatApp.backend.ChatAppBackend.repository;

import com.chatApp.backend.ChatAppBackend.models.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    @Query("{'senderId': ?#{[0]}, 'receiverId': ?#{[1]}}")
    List<Message> findMessageByID(String senderId, String receiverId);
}
