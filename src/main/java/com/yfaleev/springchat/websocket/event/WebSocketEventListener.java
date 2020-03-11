package com.yfaleev.springchat.websocket.event;

import com.yfaleev.springchat.dto.ChatMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@Slf4j
public class WebSocketEventListener {

    private static final String USER_CONNECTED_MESSAGE = "User connected : %s";
    private static final String USER_DISCONNECTED_MESSAGE = "User disconnected : %s";

    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        Principal user = getUser(event.getMessage());

        if (user != null) {
            String userConnectedMessage = String.format(USER_CONNECTED_MESSAGE, user.getName());

            log.info(userConnectedMessage);

            ChatMessageDto message = new ChatMessageDto(
                    ChatMessageDto.ChatMessageType.JOIN,
                    userConnectedMessage,
                    ChatMessageDto.SYSTEM_SENDER_NAME
            );

            messagingTemplate.convertAndSend("/topic/public", message);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal user = getUser(event.getMessage());

        if (user != null) {
            String userDisconnectedMessage = String.format(USER_DISCONNECTED_MESSAGE, user.getName());

            log.info(userDisconnectedMessage);

            ChatMessageDto message = new ChatMessageDto(
                    ChatMessageDto.ChatMessageType.LEAVE,
                    userDisconnectedMessage,
                    ChatMessageDto.SYSTEM_SENDER_NAME
            );

            messagingTemplate.convertAndSend("/topic/public", message);
        }
    }

    private Principal getUser(Message<?> message) {
        return StompHeaderAccessor.wrap(message).getUser();
    }

}
