package com.yfaleev.springchat.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yfaleev.springchat.dto.ApiResponse;
import com.yfaleev.springchat.dto.UserDto;
import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRegistrationController.class)
public class UserRegistrationControllerIntegrationTest {

    private static final String USERS_URI = "/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> captor;

    private UserDto userDto;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        userDto = new UserDto("username", "password");
    }

    @Test
    public void whenUserDtoValid_thenSuccessfulRegistration() throws Exception {
        String encodedPassword = "encoded";

        when(userService.save(any(User.class))).thenAnswer(returnsFirstArg());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn(encodedPassword);

        String jsonUserDto = mapper.writeValueAsString(userDto);
        String jsonSuccess = mapper.writeValueAsString(new ApiResponse(true));

        mockMvc.perform(
                post(USERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserDto))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonSuccess));

        verify(userService).save(captor.capture());
        User capturedUser = captor.getValue();

        assertEquals(encodedPassword, capturedUser.getPassword());
        assertEquals(userDto.getUserName(), capturedUser.getUsername());

        verify(userService, times(1)).save(capturedUser);
        verify(userService, times(1)).existsByUserName(userDto.getUserName());

        verify(passwordEncoder, times(1)).encode(userDto.getPassword());

        verifyNoMoreInteractions(userService, passwordEncoder);
    }

    @Test
    public void whenUserDtoNotValid_thenReturnResponseWithErrors() throws Exception {
        UserDto userDto = new UserDto("usr", "pswrd");

        String jsonUserDto = mapper.writeValueAsString(userDto);

        mockMvc.perform(
                post(USERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserDto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errors", hasSize(2)));

        verifyNoMoreInteractions(userService, passwordEncoder);
    }

    @Test
    public void whenUsernameAlreadyExists_thenReturnResponseWithErrors() throws Exception {
        when(userService.existsByUserName(userDto.getUserName())).thenReturn(true);

        String jsonUserDto = mapper.writeValueAsString(userDto);

        mockMvc.perform(
                post(USERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserDto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errors", hasSize(1)));

        verify(userService, times(1)).existsByUserName(userDto.getUserName());

        verifyNoMoreInteractions(userService, passwordEncoder);
    }
}
