package com.skillcart.skillcart_auth_service.config;

import com.skillcart.skillcart_auth_service.entity.AuthProvider;
import com.skillcart.skillcart_auth_service.entity.User;
import com.skillcart.skillcart_auth_service.enums.Role;
import com.skillcart.skillcart_auth_service.repository.UserRepository;
import com.skillcart.skillcart_auth_service.security.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        String registrationId =
                oauthToken
                        .getAuthorizedClientRegistrationId();

        Map<String, Object> attributes =
                oauthToken
                        .getPrincipal()
                        .getAttributes();

        String email = null;
        String name = null;

        // =========================
        // GOOGLE
        // =========================

        if (registrationId.equals("google")) {

            email =
                    (String) attributes.get("email");

            name =
                    (String) attributes.get("name");
        }


        // =========================
        // GITHUB
        // =========================

        else if (registrationId.equals("github")) {

            name =
                    (String) attributes.get("name");

            email =
                    (String) attributes.get("email");

            if (name == null) {

                name =
                        (String) attributes.get("login");
            }
        }


        // =========================
        // USER CHECK
        // =========================

        String finalEmail = email;
        String finalName = name;

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseGet(() -> {

                            User newUser =
                                    new User();

                            newUser.setName(finalName);

                            newUser.setEmail(finalEmail);

                            newUser.setRole(
                                    Role.USER
                            );

                            if (registrationId.equals("google")) {

                                newUser.setProvider(
                                        AuthProvider.GOOGLE
                                );

                            } else if (registrationId.equals("github")) {

                                newUser.setProvider(
                                        AuthProvider.GITHUB
                                );
                            }
                            return userRepository.save(
                                    newUser
                            );
                        });


        // =========================
        // GENERATE JWT
        // =========================

        String jwtToken =
                jwtService.generateJwtToken(user);


        // =========================
        // FRONTEND REDIRECT
        // =========================

        String redirectUrl =
                "http://localhost:5173/oauth-success?token="
                        + jwtToken;

        response.sendRedirect(
                redirectUrl
        );
    }
}