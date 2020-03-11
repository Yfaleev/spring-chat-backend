package com.yfaleev.springchat.controller.rest;

import com.yfaleev.springchat.dto.ApiResponse;
import com.yfaleev.springchat.dto.UserDto;
import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.service.api.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

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
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserDto userDto) {
        if (userService.existsByUserName(userDto.getUserName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, Collections.singletonList("Username already in use!")));
        }

        userService.save(
                new User(
                        userDto.getUserName(),
                        passwordEncoder.encode(userDto.getPassword())
                )
        );

        return ResponseEntity.ok(new ApiResponse(true));
    }
}
