package com.yfaleev.springchat.controller.websocket;

import com.yfaleev.springchat.dto.ChatMessageDto;
import com.yfaleev.springchat.dto.DateTimeFormat;
import com.yfaleev.springchat.model.Message;
import com.yfaleev.springchat.model.notEntityModel.UserPrincipal;
import com.yfaleev.springchat.service.api.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDto sendMessage(@Payload ChatMessageDto chatMessage, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Message message = messageService.save(
                new Message(chatMessage.getMessageText()), userPrincipal
        );

        return new ChatMessageDto(
                ChatMessageDto.ChatMessageType.CHATTING,
                message.getText(),
                userPrincipal.getUsername(),
                message.getSendDate().format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_WITH_TIME))
        );
    }
}