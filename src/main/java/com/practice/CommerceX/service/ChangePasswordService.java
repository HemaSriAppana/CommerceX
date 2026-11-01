package com.practice.CommerceX.service;



import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.practice.CommerceX.entity.User;
import com.practice.CommerceX.repository.RefreshTokenRepository;
import com.practice.CommerceX.repository.UserRepository;


@Service
public class ChangePasswordService {
 
	//configurationProperties annotation related class usage, eliminates @value() need here
//	 private  JwtProperties jwtprops;
//	 private final String secret;
//	 public AuthenticationService(JwtProperties jwtprops) {
//		 this.jwtprops=jwtprops;
//		 this.secret=jwtprops.getSecret();
//	 }
	//accessing each property
	
	
	
	    private final UserRepository userRepository;
	    private final PasswordEncoder passwordEncoder;
	    private final RefreshTokenRepository refreshTokenRepository;

	    public ChangePasswordService(
	            UserRepository userRepository,
	            PasswordEncoder passwordEncoder,
	            RefreshTokenRepository refreshTokenRepository) {

	        this.userRepository = userRepository;
	        this.passwordEncoder = passwordEncoder;
	        this.refreshTokenRepository = refreshTokenRepository;
	    }

	    public void changePassword(
	            String username,
	            String oldPassword,
	            String newPassword) {

	        User user = userRepository.findByUsername(username)
	                .orElseThrow(() ->
	                        new UsernameNotFoundException("User not found"));

	        // 1. Verify old password
	        if (!passwordEncoder.matches(
	                oldPassword,
	                user.getPassword())) {

	            throw new BadCredentialsException(
	                    "Old password is incorrect");
	        }

	        // 2. Update password
	        user.setPassword(
	                passwordEncoder.encode(newPassword));

	        // 3. Increment token version
	        user.setTokenVersion(
	                user.getTokenVersion() + 1);

	        userRepository.save(user);

	        // 4. Invalidate refresh tokens
	        refreshTokenRepository.deleteByUsername(username);
	    }
	}


