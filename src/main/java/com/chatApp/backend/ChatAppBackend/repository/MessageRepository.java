package com.chatApp.backend.ChatAppBackend.repository;

import com.chatApp.backend.ChatAppBackend.dtos.ReceiveMessageDto;
import com.chatApp.backend.ChatAppBackend.models.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    @Query("{" +
            "$or: [ " +
            "{ 'sender._id': ?0, 'receiver._id': ?1 }, " +
            "{ 'sender._id': ?1, 'receiver._id': ?0 } ] " +
            "}")
    List<Message> findMessagesBetweenUsers(String senderId,
                                           String receiverId);

    @Query(value = "{ '$and': [ " +
            "  { '$or': [ " +
            "    { '$and': [ { 'sender._id': ?0 }, { 'receiver._id': ?1 } ] }, " +
            "    { '$and': [ { 'sender._id': ?1 }, { 'receiver._id': ?0 } ] } " +
            "  ] }, " +
            "  { 'createdAt': { '$lt': ?2 } } " +   // only messages before given date
            "] }",
            fields = "{ " +
                    "'id': 1, " +
                    "'sender._id': 1, " +
                    "'receiver._id': 1, " +
                    "'text': 1, " +
                    "'image': 1, " +
                    "'createdAt': 1, " +
                    "'updatedAt': 1 " +
                    "}")
    List<Message> findMessagesBeforeDate(String senderId,
                                                   String receiverId,
                                                   Date beforeDate,
                                                   Pageable pageable);
}
