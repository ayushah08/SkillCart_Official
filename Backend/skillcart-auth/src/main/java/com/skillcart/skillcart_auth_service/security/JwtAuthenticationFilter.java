package com.skillcart.skillcart_auth_service.security;

import com.skillcart.skillcart_auth_service.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path = request.getServletPath();

        System.out.println("=================================");
        System.out.println("JWT shouldNotFilter CALLED");
        System.out.println("URI: " + path);
        System.out.println("SERVLET PATH: " + request.getServletPath());
        System.out.println("CONTEXT PATH: " + request.getContextPath());
        System.out.println("=================================");

        return path.equals("/api/v1/auth/login")
                || path.equals("/api/v1/auth/register")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.equals("/actuator/health");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("=================================");
        System.out.println("JWT FILTER ACTUALLY EXECUTING");
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("AUTH HEADER: " + request.getHeader("Authorization"));
        System.out.println("=================================");

        String authHeader =
                request.getHeader("Authorization");

        // No JWT
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String jwt =
                authHeader.substring(7);

        try {

            String username =
                    jwtService.extractUsername(jwt);

            if (
                    username != null &&
                            SecurityContextHolder
                                    .getContext()
                                    .getAuthentication() == null
            ) {

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(
                                        username
                                );

                boolean valid =
                        jwtService.validateJwtToken(
                                jwt,
                                userDetails
                        );

                if (!valid) {

                    SecurityContextHolder
                            .clearContext();

                    response.setStatus(
                            HttpServletResponse
                                    .SC_UNAUTHORIZED
                    );

                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );
            }

        } catch (Exception e) {

            SecurityContextHolder
                    .clearContext();

            System.out.println(
                    "JWT Authentication failed: "
                            + e.getClass()
                            .getSimpleName()
                            + " - "
                            + e.getMessage()
            );

            response.setStatus(
                    HttpServletResponse
                            .SC_UNAUTHORIZED
            );

            return;
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}