package com.yfaleev.springchat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatMessageHistoryDto {

    private List<ChatMessageDto> chatMessages;

    public ChatMessageHistoryDto(@JsonProperty("chatMessages") List<ChatMessageDto> chatMessages) {
        this.chatMessages = chatMessages;
    }
}
