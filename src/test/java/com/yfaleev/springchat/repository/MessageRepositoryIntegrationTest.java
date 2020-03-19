package com.yfaleev.springchat.repository;

import com.yfaleev.springchat.model.Message;
import com.yfaleev.springchat.repository.api.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(RepositoryConfig.class)
public class MessageRepositoryIntegrationTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Message message;

    @BeforeEach
    public void setUp() {
        message = new Message("text");
        message.setSendDate(LocalDateTime.now());
    }

    @Test
    public void whenMessageTextIsNull_ThenThrowException() {
        message.setText(null);
        assertThrows(DataIntegrityViolationException.class, () -> messageRepository.save(message));
    }

    @Test
    public void whenSendDateIsNull_ThenThrowException() {
        message.setSendDate(null);
        assertThrows(DataIntegrityViolationException.class, () -> messageRepository.save(message));
    }

    @Test
    public void whenUserIsNull_ThenThrowException() {
        message.setUser(null);
        assertThrows(DataIntegrityViolationException.class, () -> messageRepository.save(message));
    }
}
