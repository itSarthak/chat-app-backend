package com.chatApp.backend.ChatAppBackend.utils;

import com.chatApp.backend.ChatAppBackend.models.Friends;
import com.chatApp.backend.ChatAppBackend.models.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FriendMapper {
    public Friends toFriendsMapper(User user) {
        Date currentDate = new Date();
        Friends friend = new Friends();

        friend.set_id(user.get_id());
        friend.setFullName(user.getFullName());
        friend.setProfilePic(user.getProfilePic());
        friend.setFriendSinceDate(currentDate);
        friend.setLastInteractedDate(currentDate);
        return friend;
    }
}
