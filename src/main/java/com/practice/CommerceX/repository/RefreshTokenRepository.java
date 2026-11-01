package com.practice.CommerceX.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.CommerceX.entity.RefreshToken;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndRevokedFalse(
            String token);

    List<RefreshToken> findByFamilyId(
            String familyId);
    void deleteByUsername(String username);

//	void deleteByUserName(Long id);
}