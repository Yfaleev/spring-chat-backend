package com.yfaleev.springchat.service.impl;

import com.yfaleev.springchat.model.Message;
import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.model.notEntityModel.UserPrincipal;
import com.yfaleev.springchat.repository.api.MessageRepository;
import com.yfaleev.springchat.service.api.MessageService;
import com.yfaleev.springchat.service.api.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class MessageServiceBean implements MessageService {

    private final MessageRepository messageRepository;

    private final UserService userService;

    public MessageServiceBean(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public Message save(Message message, UserPrincipal sender) {
        User user = userService.referenceWithId(sender.getId());

        message.setUser(user);
        message.setSendDate(LocalDateTime.now());

        return messageRepository.save(message);
    }
}
