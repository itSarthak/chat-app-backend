package com.chatApp.backend.ChatAppBackend.service.socket;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineUserManager {

    private final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    public void addUser(String userId, String socketId) {
        onlineUsers.put(userId, socketId);
    }

    public void removeUser(String userId) {
        onlineUsers.remove(userId);
    }

    public Set<String> getOnlineUsers() {
        return onlineUsers.keySet();
    }

    public String getSocketIdByUserId(String userId) {
        return onlineUsers.getOrDefault(userId, null);
    }

    public boolean isOnline(String userId) {
        return onlineUsers.containsKey(userId);
    }
}
