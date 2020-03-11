package com.yfaleev.springchat.controller.websocket;

import com.yfaleev.springchat.dto.ChatMessageDto;
import com.yfaleev.springchat.dto.ChatMessageHistoryDto;
import com.yfaleev.springchat.dto.ChatUsersNamesDto;
import com.yfaleev.springchat.dto.DateTimeFormat;
import com.yfaleev.springchat.model.Message;
import com.yfaleev.springchat.model.notEntityModel.UserPrincipal;
import com.yfaleev.springchat.service.api.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class ChatController {

    private final MessageService messageService;

    private final SimpUserRegistry userRegistry;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(MessageService messageService, SimpUserRegistry userRegistry, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.userRegistry = userRegistry;
        this.simpMessagingTemplate = simpMessagingTemplate;
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

    @MessageMapping("/chat.activeUsers")
    public void showActiveUsers(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<String> userNames = userRegistry.getUsers().stream()
                .map(SimpUser::getName)
                .collect(Collectors.toList());

        simpMessagingTemplate.convertAndSendToUser(
                userPrincipal.getUsername(),
                "/queue/activeUsers",
                new ChatUsersNamesDto(userNames)
        );
    }

    @SubscribeMapping("/chat.messageHistory")
    public ChatMessageHistoryDto showMessageHistory() {
        Iterable<Message> allMessages = messageService.findAllWithUsers();

        List<ChatMessageDto> chatMessages = StreamSupport
                .stream(allMessages.spliterator(), false)
                .map(message -> new ChatMessageDto(
                        ChatMessageDto.ChatMessageType.CHATTING,
                        message.getText(),
                        message.getUser().getUsername(),
                        message.getSendDate().format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_WITH_TIME))
                )).collect(Collectors.toList());

        return new ChatMessageHistoryDto(chatMessages);
    }
}