package com.chatApp.backend.ChatAppBackend.service;

import com.chatApp.backend.ChatAppBackend.dtos.ImageUpdateDto;
import com.chatApp.backend.ChatAppBackend.dtos.UserDto;
import com.chatApp.backend.ChatAppBackend.exception.CloudinaryUploadException;
import com.chatApp.backend.ChatAppBackend.exception.ProfileImageEmptyException;
import com.chatApp.backend.ChatAppBackend.models.User;
import com.chatApp.backend.ChatAppBackend.repository.UserRepository;
import com.chatApp.backend.ChatAppBackend.utils.UserMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final CloudinaryService cloudinaryService;

    private final UserMapper userMapper;
    
    public UserService(UserRepository userRepository,
                       CloudinaryService cloudinaryService,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.userMapper = userMapper;
    }

    public UserDto updateProfile(String userEmail, ImageUpdateDto image) {
        if (image.getName().isEmpty()) {
            throw new ProfileImageEmptyException("Profile Image Name cannot be Empty");
        }
        if (image.getFile().isEmpty()) {
            throw new ProfileImageEmptyException("Profile Image File cannot be empty");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User Not found with the email" + userEmail));
        String profileUrl = cloudinaryService.uploadFile(image.getFile(), "ChatApp_profile");
        user.setProfilePic(profileUrl);
        if (user.getProfilePic() == null) {
            throw new CloudinaryUploadException("Failed to upload profile image to cloudinary");
        }
        userRepository.save(user);
        UserDto userDto = userMapper.toUserDto(user);
        return userDto;
    }

}
