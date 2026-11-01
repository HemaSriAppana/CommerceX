package com.practice.CommerceX.AuthService.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


import org.springframework.web.bind.annotation.*;

import com.practice.CommerceX.dto.ChangePasswordRequest;
import com.practice.CommerceX.dto.LoginRequest;
import com.practice.CommerceX.dto.LoginResponse;
import com.practice.CommerceX.dto.LogoutRequest;
import com.practice.CommerceX.dto.RefreshTokenRequest;
import com.practice.CommerceX.dto.RegisterUser;
import com.practice.CommerceX.entity.RefreshToken;
import com.practice.CommerceX.entity.User;
import com.practice.CommerceX.repository.UserRepository;
import com.practice.CommerceX.security.JwtService;
import com.practice.CommerceX.service.ChangePasswordService;
import com.practice.CommerceX.service.RefreshTokenService;
import com.practice.CommerceX.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final ChangePasswordService changePasswordService;
   

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,UserService userService,
            ChangePasswordService changePasswordService) {

        this.authenticationManager =
                authenticationManager;

        this.jwtService =
                jwtService;

        this.refreshTokenService =
                refreshTokenService;
        this.userService=userService;
        this.changePasswordService=changePasswordService;
    }

    @GetMapping("/")
    public String home() {
        return "CommerceX - Login Successful";
    }

    // =====================================================
    // LOGIN
    // =====================================================
    @Operation(
    	    summary = "used to login",
    	    description = "with proper credentials login can happen"
    	)

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );

            String username =
                    authentication.getName();
            
            User user=userService.findUser(username);


            Long tokenVersion =
                    user.getTokenVersion();

           
            
            // Generate access token
            String accessToken =
                    jwtService.generateAccessToken(
                            username,tokenVersion);

            // Generate refresh token
            // AND store it in database
            RefreshToken refreshToken =
                    refreshTokenService
                            .createRefreshToken(username,tokenVersion);

            return ResponseEntity.ok(
                    new LoginResponse(
                            accessToken,
                            refreshToken.getToken()
                    )
            );

        } catch (BadCredentialsException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    // =====================================================
    // REFRESH TOKEN ROTATION
    // =====================================================

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody RefreshTokenRequest request) {

        try {

            String oldToken =
                    request.getRefreshToken();

            // -----------------------------------------
            // 1. Validate JWT
            // -----------------------------------------

            String username =
                    jwtService.extractUsername(
                            oldToken);
            
            Long jwtTokenVersion =
                    jwtService.extractTokenVersion(oldToken);

            // -----------------------------------------
            // 2. Check token type
            // -----------------------------------------

            String tokenType =
                    jwtService.extractTokenType(
                            oldToken);

            if (!"refresh".equals(tokenType)) {

                throw new BadCredentialsException(
                        "Invalid refresh token");
            }

            // -----------------------------------------
            // 3. Find token in DB
            // -----------------------------------------

            RefreshToken refreshToken =
                    refreshTokenService
                            .findToken(oldToken);

            // -----------------------------------------
            // 4. Check expiration
            // -----------------------------------------

            refreshTokenService
                    .validateExpiration(
                            refreshToken);

            // -----------------------------------------
            // 5. CHECK REUSE
            // -----------------------------------------

            if (refreshToken.isRevoked()) {

                // 🚨 Token reuse detected

                refreshTokenService
                        .revokeFamily(
                            refreshToken.getFamilyId());

                throw new BadCredentialsException(
                        "Refresh token reuse detected");
            }

            // -----------------------------------------
            // 6. Revoke OLD token
            // -----------------------------------------

            refreshTokenService
                    .revokeToken(refreshToken);

            // -----------------------------------------
            // 7. Generate NEW access token
            // -----------------------------------------

            String newAccessToken =
                    jwtService.generateAccessToken(
                            username,jwtTokenVersion);

            // -----------------------------------------
            // 8. Generate NEW refresh token
            //    SAME FAMILY
            // -----------------------------------------

            RefreshToken newRefreshToken =
                    refreshTokenService
                            .createRefreshToken(
                                    username,
                                    refreshToken
                                        .getFamilyId(),jwtTokenVersion);

            // -----------------------------------------
            // 9. Return new tokens
            // -----------------------------------------

            return ResponseEntity.ok(
                    new LoginResponse(
                            newAccessToken,
                            newRefreshToken.getToken()
                    )
            );

        } catch (BadCredentialsException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody LogoutRequest request) {

        refreshTokenService.logout(
                request.getRefreshToken());

        return ResponseEntity.ok(
                "Logout successful");
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        changePasswordService.changePassword(
                username,
                request.getOldPassword(),
                request.getNewPassword());

        return ResponseEntity.ok(
                "Password changed successfully");
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> createUser(
           @Valid @RequestBody RegisterUser request) {

    	userService.saveUser( request);
    	return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
        
    }
}