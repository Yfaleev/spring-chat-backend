package com.yfaleev.springchat.service;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.yfaleev.springchat.model.Message;
import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.model.notEntityModel.UserPrincipal;
import com.yfaleev.springchat.repository.api.MessageRepository;
import com.yfaleev.springchat.service.api.UserService;
import com.yfaleev.springchat.service.impl.MessageServiceBean;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MessageServiceTest {

    @InjectMocks
    private MessageServiceBean messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @Test
    public void whenMessageSave_ThenReturnMessageWithUser() {
        Message newMessage = new Message("text");

        User user = new User("user1", "password");
        user.setId(1L);

        UserPrincipal principal = UserPrincipal
                .builder()
                .id(user.getId())
                .authorities(Collections.emptyList())
                .password(user.getPassword())
                .username(user.getUsername())
                .build();

        when(messageRepository.save(any(Message.class))).thenAnswer(returnsFirstArg());
        when(userService.referenceWithId(principal.getId())).thenReturn(user);

        LocalDateTime now = LocalDateTime.now();

        Message savedMessage = messageService.save(newMessage, principal);

        assertNotNull(savedMessage);
        assertNotNull(savedMessage.getSendDate());
        assertTrue(now.isBefore(savedMessage.getSendDate()) || now.isEqual(savedMessage.getSendDate()));
        assertThat(savedMessage.getText()).isEqualTo(newMessage.getText());
        assertThat(savedMessage.getUser()).isNotNull();
        assertThat(savedMessage.getUser().getId()).isEqualTo(user.getId());

        verify(messageRepository, times(1)).save(newMessage);
        verify(userService, times(1)).referenceWithId(principal.getId());

        verifyNoMoreInteractions(messageRepository, userService);
    }

    @Test
    public void whenFindAllWithUsers_ThenReturnMessages() {
        User user = new User("user1", "password");
        user.setId(1L);

        LocalDateTime now = LocalDateTime.now();

        List<Message> messages = new ArrayList<>();

        Message message1 = new Message("message1");
        message1.setUser(user);
        message1.setId(1L);
        message1.setSendDate(now);

        Message message2 = new Message("message2");
        message1.setUser(user);
        message1.setId(2L);
        message1.setSendDate(now);

        Message message3 = new Message("message3");
        message1.setUser(user);
        message1.setId(3L);
        message1.setSendDate(now);

        messages.add(message1);
        messages.add(message2);
        messages.add(message3);

        when(messageRepository.findAll(EntityGraphs.named(Message.GRAPH_USER))).thenReturn(messages);

        Iterable<Message> returnedMessages = messageService.findAllWithUsers();

        assertEquals(3, IterableUtil.sizeOf(returnedMessages));

        verify(messageRepository, times(1)).findAll(EntityGraphs.named(Message.GRAPH_USER));

        verifyNoMoreInteractions(messageRepository, userService);
    }
}
