package com.practice.CommerceX.integrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.practice.CommerceX.entity.User;
import com.practice.CommerceX.repository.UserRepository;
import com.practice.CommerceX.support.TestDataFactory;

import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional    // makes the transactions rollback after test completes
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;  // no mock repository

    @Test    //repository based setup
    void shouldRegisterUserEndToEnd()
            throws Exception {

        String requestBody = """
        {
            "name": "Hema_1",
            "email": "hema@gmail.com",
            "password": "password123",
            "age": 25,
            "phoneNumber": "9876543210"
        }
        """;

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isCreated());

        Optional<User> user =
                userRepository
                    .findByEmail("hema@gmail.com");  //here the user was created with email hema@gmail.com not from sql file mentioned data

        assertTrue(user.isPresent());

        assertEquals(
                "hema@gmail.com",
                user.get().getEmail()
        );
    }
    
    @Test
    void shouldFindUserByEmail() {

        User user =
                TestDataFactory.createCustomer(); // this is factory data setup

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail(
                        "hemauser@gmail.com"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "hemauser@gmail.com",
                result.get().getEmail()
        );
    }
    @Test  // this is 
    @Sql(
        scripts = "/test-data.sql", // all the queries mentioned in this file will get executed before every test method execution
        executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
        scripts = "/cleanup.sql",  // the queries mentioned in this file will get executed after every test method execution
        executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void shouldFindUser() {

        Optional<User> result =
                userRepository.findByEmail(
                        "hematest@gmail.com"
                );

        assertTrue(result.isPresent());
    }
}