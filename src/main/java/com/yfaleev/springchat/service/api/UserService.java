package com.yfaleev.springchat.service.api;

import com.yfaleev.springchat.model.User;

import java.util.Optional;

public interface UserService {

    User save(User user);

    boolean existsByUserName(String userName);

    Optional<User> findByUsername(String username);

    User referenceWithId(Long id);
}
