package com.chatApp.backend.ChatAppBackend.service;

import com.chatApp.backend.ChatAppBackend.dtos.FriendRequestDto;
import com.chatApp.backend.ChatAppBackend.exception.FriendRequestDoesNotExistsException;
import com.chatApp.backend.ChatAppBackend.exception.GlobalExceptionHandler;
import com.chatApp.backend.ChatAppBackend.models.FriendRequests;
import com.chatApp.backend.ChatAppBackend.models.Friends;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.repository.FriendRequestRepository;
import com.chatApp.backend.ChatAppBackend.repository.UserRepository;
import com.chatApp.backend.ChatAppBackend.service.socket.OnlineUserManager;
import com.chatApp.backend.ChatAppBackend.utils.FriendMapper;
import com.chatApp.backend.ChatAppBackend.utils.FriendRequestMapper;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FriendRequestService {

    private final FriendMapper friendMapper;

    private final UserRepository userRepository;

    private final SocketIOServer socketIOServer;

    private final OnlineUserManager onlineUserManager;

    private final FriendRequestMapper friendRequestMapper;

    private final FriendRequestRepository friendRequestRepository;



    public FriendRequestService(
            FriendRequestRepository friendRequestRepository,
            UserRepository userRepository,
            FriendRequestMapper friendRequestMapper,
            SocketIOServer socketIOServer,
            OnlineUserManager onlineUserManager,
            FriendMapper friendMapper
    ) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.friendRequestMapper = friendRequestMapper;
        this.socketIOServer = socketIOServer;
        this.onlineUserManager = onlineUserManager;
        this.friendMapper = friendMapper;
    }

    public List<FriendRequestDto> fetchFriendRequests(String requestSenderUserId) {
        List<FriendRequests> totalFriendRequests = friendRequestRepository.findRequestsBySenderUserId(requestSenderUserId);
        return totalFriendRequests.stream()
                .map(friendRequestMapper::toFriendRequestDto)
                .toList();
    }

    public FriendRequestDto sendFriendRequests(String requestSenderUserId, String recipientUserId) throws HttpException {
        try {
            FriendRequests existingFriendRequest = friendRequestRepository.findFriendRequestBetweenUsers(requestSenderUserId, recipientUserId);
            if (null != existingFriendRequest) {
                return friendRequestMapper.toFriendRequestDto(existingFriendRequest);
            }
        } catch (Exception err) {
            throw new HttpException(String.valueOf(err));
        }
        FriendRequests friendRequests = new FriendRequests();
        User requestSender = userRepository.findById(requestSenderUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Required Username does not exisit"));
        User requestRecipient = userRepository.findById(recipientUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Required Username does not exisit"));
        friendRequests.setRequestReceiver(requestRecipient);
        friendRequests.setRequestSender(requestSender);
        friendRequests.setIsApproved(false);

        FriendRequests savedRequest = friendRequestRepository.save(friendRequests);

        FriendRequestDto friendRequestDto = friendRequestMapper.toFriendRequestDto(savedRequest);
        String recipientSocketId = onlineUserManager.getSocketIdByUserId(recipientUserId);

        if (null != recipientSocketId) {
            socketIOServer.getClient(UUID.fromString(recipientSocketId)).sendEvent("newRequest", friendRequestDto);
        }
        return friendRequestDto;
    }

    public FriendRequestDto acceptedFriendRequest(String requestId, String  requestAcceptedUserId) {
        FriendRequests friendRequests = friendRequestRepository
                .findById(requestId).orElseThrow(() -> new FriendRequestDoesNotExistsException("The friend request you are trying to accept does not exist anymore."));
        log.info("friendRequests fetched: {}", friendRequests);
        friendRequests.setIsApproved(true);

        User requestSender = friendRequests.getRequestSender();
        User requestReceiver = friendRequests.getRequestReceiver();

        Friends senderAsFriend = friendMapper.toFriendnpsMapper(requestSender);
        Friends receiverAsFriend = friendMapper.toFriendsMapper(requestReceiver);

        if (null == requestSender.getFriendList() || requestSender.getFriendList().isEmpty()) {
            List<Friends> requestSenderFriendList = new ArrayList<>();
            requestSenderFriendList.add(receiverAsFriend);
            requestSender.setFriendList(requestSenderFriendList);
        } else {
            requestSender.getFriendList().add(receiverAsFriend);
        }

        if (null == requestReceiver.getFriendList() || requestReceiver.getFriendList().isEmpty()) {
            List<Friends> requestReceiverFriendList = new ArrayList<>();
            requestReceiverFriendList.add(senderAsFriend);
            requestReceiver.setFriendList(requestReceiverFriendList);
        } else {
            requestReceiver.getFriendList().add(senderAsFriend);
        }


        userRepository.saveAll(Arrays.asList(requestSender, requestReceiver));

        friendRequests.setRequestSender(requestSender);
        friendRequests.setRequestReceiver(requestReceiver);

        friendRequestRepository.deleteById(requestId);

        // Map DTO for response
        FriendRequestDto friendRequestDto = friendRequestMapper.toFriendRequestDto(friendRequests);

        // Notify sender in real-time
        String recipientSocketId = onlineUserManager.getSocketIdByUserId(requestSender.get_id());
        if (recipientSocketId != null) {
            socketIOServer.getClient(UUID.fromString(recipientSocketId))
                    .sendEvent("friendRequestAccepted", friendRequestDto);
        }
        return friendRequestDto;
    }


    public void rejectedFriendRequest(String requestSenderUserId, String requestAcceptedUserId) throws HttpException {
        try{
            FriendRequests friendRequests = friendRequestRepository.findFriendRequestBetweenUsers(requestSenderUserId, requestAcceptedUserId);
            friendRequestRepository.deleteById(friendRequests.getId());
        } catch (Exception e) {
            throw new HttpException(String.valueOf(e));
        }
    }

}
