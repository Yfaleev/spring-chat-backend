package com.yfaleev.springchat.service.impl;

import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.model.notEntityModel.UserPrincipal;
import com.yfaleev.springchat.service.api.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
public class CustomUserDetailsServiceBean implements UserDetailsService {

    private static final String USER_BY_NAME_NOT_FOUND = "User with name %s not found";

    private final UserService userService;

    public CustomUserDetailsServiceBean(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService
                .findByUsername(username)
                .map(this::buildUserPrincipal)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_BY_NAME_NOT_FOUND, username)));
    }

    private UserPrincipal buildUserPrincipal(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
