package com.practice.CommerceX.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.practice.CommerceX.entity.User;
import com.practice.CommerceX.repository.UserRepository;

@Service
public class CommerceUserDetailsService implements UserDetailsService{

	private  UserRepository repository;
	 public CommerceUserDetailsService(UserRepository repository) {
	        this.repository = repository;
	    }
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 User user = repository.findByUsername(username)
	                .orElseThrow(() ->
	                        new UsernameNotFoundException(
	                                "User Not Found"));

	        return new CommerceUserDetails(user);
	        }
	
}
