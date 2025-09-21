package com.chatApp.backend.ChatAppBackend.utils;

import com.chatApp.backend.ChatAppBackend.dtos.ReceiveMessageDto;
import com.chatApp.backend.ChatAppBackend.models.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public ReceiveMessageDto toReceiveMessageDto(Message message) {
        ReceiveMessageDto receiveMessageDto = new ReceiveMessageDto();
        receiveMessageDto.setText(message.getText());
        if (null != message.getImage()) {
            receiveMessageDto.setImage(message.getImage());
        }
        receiveMessageDto.setReceiverId(message.getReceiver().get_id());
        receiveMessageDto.setSenderId(message.getSender().get_id());
        receiveMessageDto.setCreatedAt(message.getCreatedAt());
        receiveMessageDto.setUpdatedAt(message.getUpdatedAt());
        return receiveMessageDto;
    }
}
