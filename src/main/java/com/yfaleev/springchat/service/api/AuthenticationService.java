package com.yfaleev.springchat.service.api;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

public interface AuthenticationService {

    UsernamePasswordAuthenticationToken getAuthenticationToken(String username, String password) throws AuthenticationException;

}
