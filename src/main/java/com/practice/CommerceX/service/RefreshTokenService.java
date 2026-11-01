package com.practice.CommerceX.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.practice.CommerceX.config.JwtProperties;
import com.practice.CommerceX.entity.RefreshToken;
import com.practice.CommerceX.repository.RefreshTokenRepository;
import com.practice.CommerceX.security.JwtService;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            JwtService jwtService,
            JwtProperties jwtProperties) {

        this.repository = repository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    // =====================================================
    // CREATE REFRESH TOKEN - NEW FAMILY
    // =====================================================

    public RefreshToken createRefreshToken(
            String username,Long tokenVersion) {

        String familyId =
                UUID.randomUUID().toString();

        return createRefreshToken(
                username,
                familyId,tokenVersion);
    }

    // =====================================================
    // CREATE REFRESH TOKEN - EXISTING FAMILY
    // =====================================================

    public RefreshToken createRefreshToken(
            String username,
            String familyId,Long tokenVersion) {

        String token =
                jwtService.generateRefreshToken(
                        username,tokenVersion);

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(token);

        refreshToken.setUsername(username);

        refreshToken.setFamilyId(familyId);

        refreshToken.setCreatedAt(
                LocalDateTime.now());

        refreshToken.setExpiresAt(
                LocalDateTime.now()
                        .plusSeconds(
                            jwtProperties
                                .getRefreshExpiration()
                                / 1000
                        ));

        refreshToken.setRevoked(false);

        return repository.save(refreshToken);
    }

    // =====================================================
    // FIND TOKEN
    // =====================================================

    public RefreshToken findToken(
            String token) {

        return repository
                .findByToken(token)
                .orElseThrow(() ->
                    new BadCredentialsException(
                        "Invalid refresh token"));
    }

    // =====================================================
    // CHECK EXPIRATION
    // =====================================================

    public void validateExpiration(
            RefreshToken refreshToken) {

        if (refreshToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BadCredentialsException(
                    "Refresh token expired");
        }
    }

    // =====================================================
    // REVOKE TOKEN
    // =====================================================

    public void revokeToken(
            RefreshToken refreshToken) {

        refreshToken.setRevoked(true);

        repository.save(refreshToken);
    }

    // =====================================================
    // REVOKE ENTIRE FAMILY
    // =====================================================

    public void revokeFamily(
            String familyId) {

        List<RefreshToken> tokens =
                repository.findByFamilyId(
                        familyId);

        for (RefreshToken token : tokens) {

            token.setRevoked(true);
        }

        repository.saveAll(tokens);
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    public void logout(String token) {

        RefreshToken refreshToken =
                findToken(token);

        revokeToken(refreshToken);
    }
}