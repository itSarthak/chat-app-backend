package com.chatApp.backend.ChatAppBackend.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfiguration {
    @Value("${socket.port}")
    private int socketPort;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(socketPort);
        config.setOrigin("https://www.chatbackend.space, https://dev.chatbackend.space");

        return new SocketIOServer(config);
    }

}
