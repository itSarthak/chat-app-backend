package com.chatApp.backend.ChatAppBackend.exception;

public class FriendRequestDoesNotExistsException extends RuntimeException{
    public FriendRequestDoesNotExistsException(String message) {
        super(message);
    }
}
