package com.yfaleev.springchat.repository;

import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.repository.api.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.persistence.PersistenceException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(RepositoryConfig.class)
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("user1", "qwerty");
    }

    @Test
    public void whenUserWithUserNameExists_ThenThrowException() {
        testEntityManager.persistAndFlush(user);

        assertThrows(PersistenceException.class, () -> testEntityManager.persistAndFlush(new User(user.getUsername(), user.getPassword())));
    }

    @Test
    public void whenUsernameIsNull_ThenThrowException() {
        user.setUsername(null);
        assertThrows(PersistenceException.class, () -> testEntityManager.persistAndFlush(user));
    }

    @Test
    public void whenPasswordIsNull_ThenThrowException() {
        user.setPassword(null);
        assertThrows(PersistenceException.class, () -> testEntityManager.persistAndFlush(user));
    }

    @Test
    public void whenFindByUsername_ThenReturnUser() {
        testEntityManager.persistAndFlush(user);

        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        assertThat(foundUser).isNotEmpty();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.get().getPassword()).isEqualTo(user.getPassword());
        assertThat(foundUser.get().getId()).isNotNull();
    }

    @Test
    public void whenFindByUsername_ThenReturnEmpty() {
        testEntityManager.persistAndFlush(user);

        Optional<User> foundUser = userRepository.findByUsername("NOT_EXISTS");

        assertThat(foundUser).isEmpty();
    }

    @Test
    public void whenExistsByUserName_ThenReturnTrue() {
        testEntityManager.persistAndFlush(user);

        boolean actual = userRepository.existsByUsername(user.getUsername());
        assertThat(actual).isTrue();
    }

    @Test
    public void whenNotExistsByUserName_ThenReturnFalse() {
        testEntityManager.persistAndFlush(user);

        boolean actual = userRepository.existsByUsername("NOT_EXISTS");
        assertThat(actual).isFalse();
    }
}
