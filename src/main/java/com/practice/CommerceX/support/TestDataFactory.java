package com.practice.CommerceX.support;

import com.practice.CommerceX.entity.User;

public class TestDataFactory {

    public static User createCustomer() {

        User user = new User();

        user.setUsername("hemauser");
        user.setEmail("hemauser@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");
        user.setTokenVersion(0L);

        return user;
    }

    public static User createAdmin() {

        User user = new User();

        user.setUsername("admin");
        user.setEmail("admin@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole("ADMIN");
        user.setTokenVersion(0L);

        return user;
    }
}