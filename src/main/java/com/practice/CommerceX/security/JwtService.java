package com.practice.CommerceX.security;


import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.practice.CommerceX.config.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {

        this.jwtProperties = jwtProperties;

        this.secretKey =
                Keys.hmacShaKeyFor(
                        jwtProperties
                                .getSecret()
                                .getBytes());
    }

    // Generate JWT after successful login
    public String generateToken(String username) {

        Date issuedAt = new Date();

        Date expiration =
                new Date(
                        issuedAt.getTime()
                                + jwtProperties.getExpiration()
                );

        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
    
    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(
            String token,
            UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        Date expiration =
                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getExpiration();

        return expiration.before(new Date());
    }
    
//    public String generateAccessToken(String username) {
//
//        Date issuedAt = new Date();
//
//        Date expiration =
//                new Date(
//                        issuedAt.getTime()
//                                + jwtProperties.getAccessExpiration()
//                );
//
//        return Jwts.builder()
//                .subject(username)
//                .issuedAt(issuedAt)
//                .expiration(expiration)
//                .signWith(secretKey)
//                .compact();
//    }
//    
//    public String generateRefreshToken(String username) {
//
//        Date issuedAt = new Date();
//
//        Date expiration =
//                new Date(
//                        issuedAt.getTime()
//                                + jwtProperties.getRefreshExpiration()
//                );
//
//        return Jwts.builder()
//                .subject(username)
//                .issuedAt(issuedAt)
//                .expiration(expiration)
//                .signWith(secretKey)
//                .compact();
//    }
    public String generateRefreshToken(String username,Long tokenVersion) {

        return generateToken(
                username,
                "refresh",
                jwtProperties.getRefreshExpiration(),tokenVersion);
    }
    public String generateAccessToken(String username,Long tokenVersion) {

        return generateToken(
                username,
                "access",
                jwtProperties.getAccessExpiration(),tokenVersion);
    }
    private String generateToken(
            String username,
            String tokenType,
            long expirationTime,long  tokenVersion) {

        Date issuedAt = new Date();

        Date expiration =
                new Date(
                        issuedAt.getTime()
                                + expirationTime);

        return Jwts.builder()
                .subject(username)
                .claim("type", tokenType) // to differentiate token
                .claim("tokenVersion", tokenVersion)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
    public String extractTokenType(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("type", String.class);
    }
    
    public boolean validateRefreshToken(String token) {

        String tokenType =
                extractTokenType(token);

        if (!"refresh".equals(tokenType)) {
            return false;
        }

        return !isTokenExpired(token);
    }
    
    public Long extractTokenVersion(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("tokenVersion", Long.class);
    }
    
}