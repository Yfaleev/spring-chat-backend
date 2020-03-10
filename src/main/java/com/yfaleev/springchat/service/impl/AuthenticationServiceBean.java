package com.yfaleev.springchat.service.impl;

import com.yfaleev.springchat.service.api.AuthenticationService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional
public class AuthenticationServiceBean implements AuthenticationService {

    private static final String USERNAME_EMPTY = "Username was null or empty.";
    private static final String PASSWORD_EMPTY = "Password was null or empty.";
    private static final String BAD_CREDENTIALS = "Bad credentials for user %s";

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceBean(UserDetailsService customUserDetailsServiceBean, PasswordEncoder passwordEncoder) {
        this.userDetailsService = customUserDetailsServiceBean;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthenticationToken(String username, String password) throws AuthenticationException {
        if (isBlank(username)) {
            throw new AuthenticationCredentialsNotFoundException(USERNAME_EMPTY);
        }
        if (isBlank(password)) {
            throw new AuthenticationCredentialsNotFoundException(PASSWORD_EMPTY);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!isPasswordMatchEncoded(password, userDetails.getPassword(), passwordEncoder)) {
            throw new BadCredentialsException(String.format(BAD_CREDENTIALS, username));
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    private boolean isPasswordMatchEncoded(String inputPassword, String encodedPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(inputPassword, encodedPassword);
    }
}
