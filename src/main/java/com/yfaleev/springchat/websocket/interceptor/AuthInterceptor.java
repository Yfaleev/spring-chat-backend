package com.yfaleev.springchat.websocket.interceptor;

import com.yfaleev.springchat.service.api.AuthenticationService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthInterceptor implements ChannelInterceptor {

    private static final String USERNAME_HEADER = "login";
    private static final String PASSWORD_HEADER = "password";

    private final AuthenticationService authenticationService;

    public AuthInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) throws AuthenticationException {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String username = accessor.getFirstNativeHeader(USERNAME_HEADER);
            String password = accessor.getFirstNativeHeader(PASSWORD_HEADER);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = authenticationService.getAuthenticationToken(username, password);

            accessor.setUser(usernamePasswordAuthenticationToken);
        }

        return message;
    }
}