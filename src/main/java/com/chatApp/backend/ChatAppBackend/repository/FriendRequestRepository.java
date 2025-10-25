package com.chatApp.backend.ChatAppBackend.repository;

import com.chatApp.backend.ChatAppBackend.models.FriendRequests;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FriendRequestRepository extends MongoRepository<FriendRequests, String> {

    @Query("{ 'requestReceiver._id': ?0 }")
    List<FriendRequests> findRequestsBySenderUserId(String userId);

    @Query("{ 'requestSender._id': ?0, 'requestReceiver._id': ?1 }")
    FriendRequests findFriendRequestBetweenUsers(String requestSenderUserId, String recipientUserId);
}
