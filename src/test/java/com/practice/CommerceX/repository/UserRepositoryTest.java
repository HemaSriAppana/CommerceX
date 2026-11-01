package com.practice.CommerceX.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.practice.CommerceX.entity.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {

        User user = new User();

        user.setUsername("Hema");
        user.setEmail("hema@gmail.com");
        user.setPassword("encodedPassword");

        User savedUser =
                userRepository.save(user);

        assertNotNull(savedUser.getId());

        assertEquals(
                "hema@gmail.com",
                savedUser.getEmail()
        );
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {

        User user = new User();

        user.setUsername("Hema");
        user.setEmail("hema@gmail.com");
        user.setPassword("encodedPassword");
       

        userRepository.save(user);

        boolean result =
                userRepository.existsByEmail(
                        "hema@gmail.com"
                );

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {

        boolean result =
                userRepository.existsByEmail(
                        "unknown@gmail.com"
                );

        assertFalse(result);
    }

    @Test
    void shouldFindUserByEmail() {

        User user = new User();

        user.setUsername("Hema_Sri");
        user.setEmail("hema@gmail.com");
        user.setPassword("encodedPassword");
       

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByUsername(
                        "Hema_Sri"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "Hema_Sri",
                result.get().getUsername()
        );
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {

        Optional<User> result =
                userRepository.findByUsername(
                        "Hema_Sri"
                );

        assertTrue(result.isEmpty());
    }
}