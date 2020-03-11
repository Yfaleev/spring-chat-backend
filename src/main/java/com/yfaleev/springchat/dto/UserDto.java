package com.yfaleev.springchat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Size;

@Getter
public class UserDto {

    @Size(min = 4, max = 30, message = "username length must be between {min} and {max} characters")
    private String userName;

    @Size(min = 8, max = 30, message = "password length must be between {min} and {max} characters")
    private String password;

    @JsonCreator
    public UserDto(@JsonProperty("userName") String userName, @JsonProperty("password") String password) {
        this.userName = userName;
        this.password = password;
    }
}
