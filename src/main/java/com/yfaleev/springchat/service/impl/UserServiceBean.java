package com.yfaleev.springchat.service.impl;

import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.repository.api.UserRepository;
import com.yfaleev.springchat.service.api.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceBean implements UserService {

    private final UserRepository userRepository;

    public UserServiceBean(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUsername(userName);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User referenceWithId(Long id) {
        return userRepository.getOne(id);
    }
}
