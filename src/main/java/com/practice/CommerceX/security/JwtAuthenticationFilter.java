package com.practice.CommerceX.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CommerceUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CommerceUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /*
     * These endpoints don't require an Access JWT.
     *
     * Login    -> user doesn't have JWT yet
     * Refresh  -> uses Refresh Token
     * Logout   -> uses Refresh Token
     * Register -> user isn't authenticated yet
     */
    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String path = request.getServletPath();

        return path.equals("/auth/login")
                || path.equals("/auth/refresh")
                || path.equals("/auth/logout")
                || path.equals("/register");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        /*
         * No Authorization header
         * or not a Bearer token.
         *
         * Let the request continue.
         */
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " prefix
        String jwt =
                authHeader.substring(7);

        try {

            // Extract username from JWT
            String username =
                    jwtService.extractUsername(jwt);

            /*
             * Authenticate only if there isn't
             * already an Authentication object.
             */
            if (username != null
                    && SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

                // Load user from database
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                /*
                 * Validate:
                 * - Signature
                 * - Expiration
                 * - Username
                 */
                if (jwtService.validateToken(
                        jwt,
                        userDetails)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Put authenticated user
                     * into SecurityContext.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication);
                }
            }

        } catch (Exception e) {

            /*
             * JWT is invalid/expired/etc.
             */
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        /*
         * Continue to the next filter/controller.
         */
        filterChain.doFilter(request, response);
    }
}