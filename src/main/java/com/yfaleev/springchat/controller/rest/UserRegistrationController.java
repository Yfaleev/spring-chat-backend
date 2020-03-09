package com.yfaleev.springchat.controller.rest;

import com.yfaleev.springchat.dto.UserDto;
import com.yfaleev.springchat.exception.BadRequestException;
import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.service.api.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserRegistrationController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserRegistrationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserDto userDto) {
        if (userService.existsByUserName(userDto.getUserName())) {
            throw new BadRequestException("Username already in use!");
        }

        userService.save(
                new User(
                        userDto.getUserName(),
                        passwordEncoder.encode(userDto.getPassword())
                )
        );
    }
}
