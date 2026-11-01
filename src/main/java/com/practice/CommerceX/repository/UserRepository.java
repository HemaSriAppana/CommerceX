package com.practice.CommerceX.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.practice.CommerceX.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);


	boolean existsByEmail(String email);
	
//	@Query("""
//		    SELECT u
//		    FROM User u
//		    WHERE u.username = :username
//		      AND u.role = :role
//		""")
//		Optional<User> findByUsernameAndRole(
//		        @Param("username") String username,
//		        @Param("role") String role
//		);
	
	Optional<User> findByUsernameAndRole(
	        String username,
	        String role
	);
		
	

}
