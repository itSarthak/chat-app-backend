package com.chatApp.backend.ChatAppBackend.exception;

public class UserCreationFailureException extends RuntimeException {
    public UserCreationFailureException(String message) {
        super(message);
    }
}
