package com.yfaleev.springchat.service.api;

import com.yfaleev.springchat.model.Message;
import com.yfaleev.springchat.model.notEntityModel.UserPrincipal;

public interface MessageService {

    Message save(Message message, UserPrincipal sender);

    Iterable<Message> findAllWithUsers();
}
