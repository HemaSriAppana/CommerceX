package com.practice.CommerceX.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.CommerceX.dto.RegisterUser;
import com.practice.CommerceX.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user/")
public class UserController {
	
	private final UserService userService;
	public UserController(UserService userService) {
		this. userService=userService;
	}

	@GetMapping("/")
    public String home() {
        return "CommerceX - Login Successful";
    }

    @GetMapping("/me")
    public String currentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        return userDetails.getUsername();
    }
    @GetMapping("/profile")
    public String profile() {

        return "User Profile";
    }
    
    
}