package com.practice.CommerceX.security;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CommerceAuthenticationProvider implements AuthenticationProvider {

    private final CommerceUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CommerceAuthenticationProvider(
            CommerceUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        // Extract username from request
        String username = authentication.getName();

        // Extract password from request
        String password = authentication.getCredentials().toString();

        // Load user from database
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);

        // Compare entered password with stored password
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid username or password");
        }

        // Authentication successful a token will be  returned
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> authentication) {

        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);

    }

}