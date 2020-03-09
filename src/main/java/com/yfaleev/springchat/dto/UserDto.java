package com.yfaleev.springchat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UserDto {

    @Size(min = 4, max = 30, message = "username length must be between {min} and {max} characters")
    private String userName;

    @Size(min = 8, max = 30, message = "password length must be between {min} and {max} characters")
    private String password;

    @JsonCreator
    public UserDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
