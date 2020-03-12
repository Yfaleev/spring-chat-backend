package com.yfaleev.springchat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatUsersNamesDto {

    private List<String> userNames;

    public ChatUsersNamesDto(@JsonProperty("userNames") List<String> userNames) {
        this.userNames = userNames;
    }
}
