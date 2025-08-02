package com.chatApp.backend.ChatAppBackend.utils;

import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
import com.chatApp.backend.ChatAppBackend.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setProfilePic(user.getProfilePic());
        dto.setCreatedAt(user.getCreatedAt());
        dto.set_id(user.get_id());
        dto.setChattyUsername(user.getChattyUserName());

        return dto;
    }
}
