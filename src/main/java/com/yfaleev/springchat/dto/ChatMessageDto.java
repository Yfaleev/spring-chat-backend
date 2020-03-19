package com.yfaleev.springchat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
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

    public ChatMessageDto(String messageText) {
        this.messageText = messageText;
    }

    @JsonCreator
    public ChatMessageDto(
            @JsonProperty("messageType") ChatMessageType messageType,
            @JsonProperty("messageText") String messageText,
            @JsonProperty("sender") String sender,
            @JsonProperty("sendDate") String sendDate) {
        this.messageType = messageType;
        this.messageText = messageText;
        this.sender = sender;
        this.sendDate = sendDate;
    }
}
