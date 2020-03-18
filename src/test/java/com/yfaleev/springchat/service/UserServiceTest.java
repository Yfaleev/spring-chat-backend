package com.yfaleev.springchat.service;

import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.repository.api.UserRepository;
import com.yfaleev.springchat.service.impl.UserServiceBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceBean userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("username", "password");
    }

    @Test
    public void whenSave_ThenReturnUser() {
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        User saved = userService.save(user);

        assertNotNull(saved);
        assertThat(saved.getPassword()).isEqualTo(user.getPassword());
        assertThat(saved.getUsername()).isEqualTo(user.getUsername());

        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void whenUserExistsByUsername_ThenReturnTrue() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        boolean exists = userService.existsByUserName(user.getUsername());
        assertThat(exists).isTrue();

        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void whenUserNotExistsByUsername_ThenReturnFalse() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);

        boolean exists = userService.existsByUserName(user.getUsername());
        assertThat(exists).isFalse();

        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void whenUserFoundByUsername_ThenReturnUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByUsername(this.user.getUsername());

        assertThat(foundUser).isNotEmpty();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.get().getPassword()).isEqualTo(user.getPassword());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void whenUserNotFoundByUsername_ThenReturnEmpty() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findByUsername(this.user.getUsername());

        assertThat(foundUser).isEmpty();

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void whenReferenceWithId_ThenReturnUserWithId() {
        User userWithId = new User();
        userWithId.setId(1L);

        when(userRepository.getOne(userWithId.getId())).thenReturn(userWithId);

        User returnedUserWithId = userService.referenceWithId(userWithId.getId());

        assertThat(returnedUserWithId).isNotNull();
        assertThat(returnedUserWithId.getId()).isEqualTo(userWithId.getId());

        verify(userRepository, times(1)).getOne(userWithId.getId());
        verifyNoMoreInteractions(userRepository);
    }
}
