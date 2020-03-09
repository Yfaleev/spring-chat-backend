package com.yfaleev.springchat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UserDto {

    @NotBlank
    @Size(min = 4, max = 30)
    private String userName;

    @NotBlank
    @Size(min = 8, max = 30)
    private String password;

    @JsonCreator
    public UserDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
