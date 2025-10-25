package com.chatApp.backend.ChatAppBackend.controller;

import com.chatApp.backend.ChatAppBackend.dtos.FriendRequestDto;
import com.chatApp.backend.ChatAppBackend.service.FriendRequestService;
import com.chatApp.backend.ChatAppBackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.core5.http.HttpException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/friends")
@RestController
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    private final UserService userService;

    public FriendRequestController(
            FriendRequestService friendRequestService,
            UserService userService
    ) {
        this.friendRequestService = friendRequestService;
        this.userService = userService;
    }


    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestDto>> getAllFriendRequests(HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        String userId = userService.fetchUserIdFromMail(userEmail);
        List<FriendRequestDto> totalFriendRequests= friendRequestService.fetchFriendRequests(userId);
        return ResponseEntity.ok().body(totalFriendRequests);
    }

    @PostMapping("/requests/{recipient_id}")
    public ResponseEntity<FriendRequestDto> sendFriendRequests(
            HttpServletRequest request,
            @PathVariable String recipient_id) throws HttpException {
        String userEmail = request.getUserPrincipal().getName();
        String userId = userService.fetchUserIdFromMail(userEmail);
        FriendRequestDto friendRequestDto = friendRequestService.sendFriendRequests(userId, recipient_id);
        return ResponseEntity.ok().body(friendRequestDto);
    }

    @PutMapping("/requests/accepted/{request_id}")
    public ResponseEntity<FriendRequestDto> acceptFriendRequest(
            HttpServletRequest request,
            @PathVariable String request_id
    ) {
        String userEmail = request.getUserPrincipal().getName();
        String userId = userService.fetchUserIdFromMail(userEmail);
        FriendRequestDto friendRequestDto = friendRequestService.acceptedFriendRequest(request_id, userId);
        return ResponseEntity.ok().body(friendRequestDto);
    }

    @PutMapping("/requests/rejected/{sender_id}")
    public ResponseEntity<String> rejectFriendRequest(
            HttpServletRequest request,
            @PathVariable String sender_id
    ) {
        String userEmail = request.getUserPrincipal().getName();
        String userId = userService.fetchUserIdFromMail(userEmail);
        FriendRequestDto friendRequestDto = friendRequestService.acceptedFriendRequest(sender_id, userId);
        return ResponseEntity.status(204).body("Successfully Rejected");
    }
}
