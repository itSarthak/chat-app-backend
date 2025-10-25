package com.chatApp.backend.ChatAppBackend.utils;

import com.chatApp.backend.ChatAppBackend.dtos.FriendRequestDto;
import com.chatApp.backend.ChatAppBackend.models.FriendRequests;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestMapper {

    private final UserMapper userMapper;

    public FriendRequestMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public FriendRequestDto toFriendRequestDto(FriendRequests friendRequests) {
        FriendRequestDto friendRequestDto = new FriendRequestDto();
        friendRequestDto.setId(friendRequests.getId());
        friendRequestDto.setRequestReceiver(userMapper.toUserDto(friendRequests.getRequestReceiver()));
        friendRequestDto.setRequestSender(userMapper.toUserDto(friendRequests.getRequestSender()));
        friendRequestDto.setRequestSince(friendRequests.getRequestSince());
        friendRequestDto.setRequestRespondDate(friendRequests.getRequestRespondDate());
        return friendRequestDto;
    }
}
