package com.skillcart_resumeservice.socialservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("====================================");
        System.out.println("SOCIAL JWT FILTER CALLED");
        System.out.println("METHOD: " + request.getMethod());
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("CONTENT TYPE: " + request.getContentType());
        System.out.println("AUTH HEADER: " +
                (request.getHeader("Authorization") != null
                        ? "PRESENT"
                        : "MISSING"));
        System.out.println("====================================");

        // Allow CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            System.out.println("OPTIONS REQUEST - SKIPPING JWT");

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String authHeader =
                request.getHeader("Authorization");

        if (
                authHeader == null ||
                        !authHeader.startsWith("Bearer ")
        ) {

            System.out.println("NO VALID BEARER TOKEN");

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        try {

            String token =
                    authHeader.substring(7);

            System.out.println(
                    "VALIDATING JWT..."
            );

            boolean valid =
                    jwtService.validateToken(token);

            System.out.println(
                    "JWT VALID: " + valid
            );

            if (!valid) {

                SecurityContextHolder.clearContext();

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                return;
            }

            String username =
                    jwtService.extractUsername(token);

            String userId =
                    jwtService.extractUserId(token);

            System.out.println(
                    "JWT USERNAME: " + username
            );

            System.out.println(
                    "JWT USER ID: " + userId
            );

            if (
                    username != null &&
                            userId != null &&
                            SecurityContextHolder
                                    .getContext()
                                    .getAuthentication() == null
            ) {

                UUID uuid =
                        UUID.fromString(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                uuid,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority(
                                                "ROLE_USER"
                                        )
                                )
                        );

                authentication.setDetails(
                        username
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );

                System.out.println(
                        "JWT AUTHENTICATION SUCCESS"
                );

            } else {

                System.out.println(
                        "JWT DATA MISSING OR USER ALREADY AUTHENTICATED"
                );
            }

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            System.out.println("====================================");
            System.out.println("JWT AUTHENTICATION FAILED");
            System.out.println(
                    "ERROR TYPE: "
                            + e.getClass().getName()
            );
            System.out.println(
                    "ERROR MESSAGE: "
                            + e.getMessage()
            );
            e.printStackTrace();
            System.out.println("====================================");

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }

        System.out.println(
                "FINAL AUTHENTICATION: " +
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
        );
        filterChain.doFilter(
                request,
                response
        );
    }
}