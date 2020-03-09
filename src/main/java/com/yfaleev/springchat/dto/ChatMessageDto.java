package com.yfaleev.springchat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    public static final String SYSTEM_SENDER_NAME = "SYSTEM";

    public enum ChatMessageType {
        CHATTING,
        JOIN,
        LEAVE
    }

    private ChatMessageType messageType;

    private String messageText;

    private String sender;

    private String sendDate;

    public ChatMessageDto(ChatMessageType messageType, String messageText, String sender) {
        this(messageType,
                messageText,
                sender,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_WITH_TIME))
        );
    }
}
