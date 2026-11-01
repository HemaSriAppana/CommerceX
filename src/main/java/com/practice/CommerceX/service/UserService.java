package com.practice.CommerceX.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.practice.CommerceX.dto.RegisterUser;
import com.practice.CommerceX.entity.User;
import com.practice.CommerceX.exception.UserAlreadyExistsException;
import com.practice.CommerceX.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepo;
private final PasswordEncoder passwordEncoder;

	
	public UserService(UserRepository userRepo,PasswordEncoder passwordEncoder) {
		this.userRepo =userRepo;
		this.passwordEncoder= passwordEncoder;
	}
	
	public void saveUser(RegisterUser registerUser) {

	    if (userRepo.existsByEmail(registerUser.getEmail())) {

	        throw new UserAlreadyExistsException(
	                "User with this email already exists"
	        );
	    }

	    User user = new User();

	    user.setUsername(registerUser.getName());
	    user.setEmail(registerUser.getEmail());

	    user.setPassword(
	        passwordEncoder.encode(
	            registerUser.getPassword()
	        )
	    );

	    userRepo.save(user);
	}
	
	public User findUser(String username) {
		User user= userRepo
        .findByUsername(username)
        .orElseThrow(() ->
                new UsernameNotFoundException(
                        "User not found"));
		return user;
	}
}
