package com.chatApp.backend.ChatAppBackend.websocket;

import com.chatApp.backend.ChatAppBackend.dtos.MessageDto;
import com.chatApp.backend.ChatAppBackend.service.socket.OnlineUserManager;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SocketModule {

    private final SocketIOServer server;
    private final OnlineUserManager onlineUserManager;

    public SocketModule (
            SocketIOServer server,
            OnlineUserManager onlineUserManager
    ) {
        this.server = server;
        this.onlineUserManager = onlineUserManager;
    }

    @PostConstruct
    private void startServer() {
        server.addConnectListener(client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId") ;
            if(userId != null) {
                log.info("User connected: " + userId + ", session: " + client.getSessionId());
                onlineUserManager.addUser(userId, String.valueOf(client.getSessionId()));
                server.getBroadcastOperations().sendEvent("getOnlineUsers", onlineUserManager.getOnlineUsers());
            }
        });

        server.addDisconnectListener(client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            if (userId != null) {
                log.info("User disconnected: " + userId);
                onlineUserManager.removeUser(userId);
                server.getBroadcastOperations().sendEvent("getOnlineUsers", onlineUserManager.getOnlineUsers());
            }
        });
        server.start();
        log.info("SocketIO Started");
    }

    @PreDestroy
    private void stopServer() {
        server.stop();
    }

}
