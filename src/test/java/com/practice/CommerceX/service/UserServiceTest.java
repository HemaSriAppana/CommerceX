package com.practice.CommerceX.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.practice.CommerceX.dto.RegisterUser;
import com.practice.CommerceX.entity.User;
import com.practice.CommerceX.exception.UserAlreadyExistsException;
import com.practice.CommerceX.repository.UserRepository;
@ExtendWith(MockitoExtension.class)

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
    private UserRepository userRepo;


    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateCorrectUser() {

        // Arrange
        RegisterUser request = new RegisterUser();

        request.setName("Hema");
        request.setEmail("hema@gmail.com");
        request.setPassword("password123");
        request.setAge(25);
        request.setPhoneNumber("9876543210");

        when(userRepo.existsByEmail("hema@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        // Act
        userService.saveUser(request);

        // Capture the User passed to save()
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepo)
                .save(userCaptor.capture());

        // Get captured User
        User capturedUser = userCaptor.getValue();

        // Assert
        assertEquals("Hema", capturedUser.getUsername());
        assertEquals("hema@gmail.com", capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
    }
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        RegisterUser request = new RegisterUser();

        request.setName("Hema");
        request.setEmail("hema@gmail.com");
        request.setPassword("password123");
        request.setAge(25);
        request.setPhoneNumber("9876543210");

        when(userRepo.existsByEmail("hema@gmail.com"))
                .thenReturn(true);

        // Act + Assert
        UserAlreadyExistsException exception =
                assertThrows(
                        UserAlreadyExistsException.class,
                        () -> userService.saveUser(request)
                );

        // Verify exception message
        assertEquals(
                "User with this email already exists",
                exception.getMessage()
        );

        // Verify email was checked
        verify(userRepo)
                .existsByEmail("hema@gmail.com");

        // Verify user was NOT saved
        verify(userRepo, never())
                .save(any(User.class));
    }
    
//    @Test
//    void shouldFindUserByUsernameAndRole() {
//
//        User user = new User();
//
//        user.setUsername("hemaCustomer");
//        user.setEmail("hema@gmail.com");
//        user.setPassword("encodedPassword");
//        user.setRole("CUSTOMER");
//       // saveAndFlush instructs that the all changes in persist stage should be moved to Db(should get executed) and  new save response also should present in db
//        userRepo.saveAndFlush(user);
//
//        //below method used custom JPQL query
//        Optional<User> result =
//        		userRepo.findByUsernameAndRole(
//                        "hemaCustomer",
//                        "CUSTOMER"
//                );
//
//        assertTrue(result.isPresent());
//
//        assertEquals(
//                "hemaCustomer",
//                result.get().getUsername()
//        );
//    }
    
    @Test
    void shouldFindUserByUsernameAndRole() {

        User user = new User();

        user.setUsername("hemaCustomer");
        user.setEmail("hema@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole("CUSTOMER");
        
        when(userRepo.findByUsernameAndRole(
                "hemaCustomer",
                "CUSTOMER"
        )).thenReturn(Optional.of(user));
        
//        when(userRepo.save(user)).thenReturn(user);
//
//        User savedUser = userRepo.save(user);
        // Then test username + role
        Optional<User> result =
                userRepo.findByUsernameAndRole(
                        "hemaCustomer",
                        "CUSTOMER"
                );

        System.out.println("By username + role = " + result);

        assertTrue(result.isPresent());
    }
    @Test
    void shouldReturnEmptyForWrongRole() {

        User user = new User();

        user.setUsername("hema");
        user.setEmail("hema@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole("CUSTOMER");

        userRepo.saveAndFlush(user);

        Optional<User> result =
                userRepo.findByUsernameAndRole(
                        "hema",
                        "ADMIN"
                );

        assertTrue(result.isEmpty());
    }
}