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
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Read Authorization Header
        String authHeader = request.getHeader("Authorization");

        // 2. Check if header is missing or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract JWT Token
        String jwt = authHeader.substring(7);

        // 4. Extract Username
        String username = jwtService.extractUsername(jwt);
        String role =
                jwtService.extractRole(jwt);

        // 5. Continue only if user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load UserDetails from Database
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // 7. Validate JWT
            if (jwtService.validateJwtToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. Store Authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 9. Continue Filter Chain
        filterChain.doFilter(request, response);
    }
}