package com.yfaleev.springchat.service;

import com.yfaleev.springchat.model.notEntityModel.UserPrincipal;
import com.yfaleev.springchat.service.impl.AuthenticationServiceBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationServiceBean authenticationService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserDetails principal;

    @BeforeEach
    public void setUp() {
        principal = UserPrincipal
                .builder()
                .id(1L)
                .authorities(Collections.emptyList())
                .password("password")
                .username("username")
                .build();
    }

    @Test
    public void whenUsernameAndPasswordMatches_ThenReturnToken() {
        when(userDetailsService.loadUserByUsername(principal.getUsername())).thenReturn(principal);
        when(passwordEncoder.matches(any(), anyString())).thenReturn(true);

        UsernamePasswordAuthenticationToken authenticationToken = authenticationService.getAuthenticationToken(principal.getUsername(), principal.getPassword());

        assertNotNull(authenticationToken);
        assertNotNull(authenticationToken.getPrincipal());
        assertThat(authenticationToken.getPrincipal()).isInstanceOf(UserDetails.class);

        UserDetails returnedPrincipal = (UserDetails) authenticationToken.getPrincipal();

        assertThat(returnedPrincipal.getPassword()).isEqualTo(principal.getPassword());
        assertThat(returnedPrincipal.getUsername()).isEqualTo(principal.getUsername());
        assertThat(returnedPrincipal.getAuthorities()).isEqualTo(principal.getAuthorities());

        assertThat(authenticationToken.getAuthorities()).isEqualTo(principal.getAuthorities());

        verify(userDetailsService, times(1)).loadUserByUsername(principal.getUsername());
        verify(passwordEncoder, times(1)).matches(principal.getPassword(), principal.getPassword());

        verifyNoMoreInteractions(userDetailsService, passwordEncoder);
    }

    @Test
    public void whenUsernameEmpty_ThenThrowException() {
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> authenticationService.getAuthenticationToken("", principal.getPassword())
        );

        verifyNoInteractions(userDetailsService, passwordEncoder);
    }

    @Test
    public void whenPasswordEmpty_ThenThrowException() {
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> authenticationService.getAuthenticationToken(principal.getUsername(), "")
        );

        verifyNoInteractions(userDetailsService, passwordEncoder);
    }

    @Test
    public void whenUserNameNotFound_ThenThrowException() {
        when(userDetailsService.loadUserByUsername(principal.getUsername())).thenThrow(UsernameNotFoundException.class);

        assertThrows(
                UsernameNotFoundException.class,
                () -> authenticationService.getAuthenticationToken(principal.getUsername(), principal.getPassword())
        );

        verify(userDetailsService, times(1)).loadUserByUsername(principal.getUsername());

        verifyNoMoreInteractions(userDetailsService, passwordEncoder);
    }

    @Test
    public void whenPasswordNotMatches_ThenThrowException() {
        when(userDetailsService.loadUserByUsername(principal.getUsername())).thenReturn(principal);
        when(passwordEncoder.matches(any(), anyString())).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.getAuthenticationToken(principal.getUsername(), principal.getPassword())
        );

        verify(userDetailsService, times(1)).loadUserByUsername(principal.getUsername());
        verify(passwordEncoder, times(1)).matches(principal.getPassword(), principal.getPassword());

        verifyNoMoreInteractions(userDetailsService, passwordEncoder);
    }
}
