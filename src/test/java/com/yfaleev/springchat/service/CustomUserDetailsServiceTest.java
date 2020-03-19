package com.yfaleev.springchat.service;

import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.service.api.UserService;
import com.yfaleev.springchat.service.impl.CustomUserDetailsServiceBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsServiceBean customUserDetailsService;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("username", "password");
    }

    @Test
    public void whenLoadExistingUserByUsername_ThenReturnMatchingUserDetails() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        assertNotNull(userDetails);
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.getAuthorities()).isEmpty();

        verify(userService, times(1)).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void whenLoadNotExistingUserByUsername_ThenThrowException() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(user.getUsername()));

        verify(userService, times(1)).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userService);
    }
}
