package com.example.javatokotlin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // C
    @Test
    void testCreateUser() {
        // given
        String name = "James";
        String email = "james@gmail.com";

        User user = new User(1L, name, email);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        User actual = userService.createUser(name, email);

        // then
        assertNotNull(actual);
        assertEquals(name, actual.getName());
        assertEquals(email, actual.getEmail());
    }

    @Test
    void testGetUser() {
        // given
        Long userId = 1L;
        User user = new User(1L, "James", "james@gmail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        Optional<User> actual = userService.getUserById(userId);

        // then
        assertTrue(actual.isPresent());
    }
}
