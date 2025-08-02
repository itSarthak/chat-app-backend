package com.chatApp.backend.ChatAppBackend.repository;

import com.chatApp.backend.ChatAppBackend.models.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    @Query("{$or: [ { 'sender._id': ?0, 'receiver._id': ?1 }, { 'sender._id': ?1, 'receiver._id': ?0 } ] }")
    List<Message> findMessagesBetweenUsers(String senderId, String receiverId);
}
