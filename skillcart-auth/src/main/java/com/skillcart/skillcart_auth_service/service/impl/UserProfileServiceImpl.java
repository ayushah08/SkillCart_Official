package com.skillcart.skillcart_auth_service.service.impl;

import com.skillcart.skillcart_auth_service.dto.DetailResponse;
import com.skillcart.skillcart_auth_service.entity.User;
import com.skillcart.skillcart_auth_service.repository.UserRepository;
import com.skillcart.skillcart_auth_service.security.JwtService;
import com.skillcart.skillcart_auth_service.service.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    public DetailResponse getUserDetails() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
       Optional<User> user =  userRepository.findByUsername(username);

       if (user.isEmpty()) {
           throw new UsernameNotFoundException("User not found");
       }

        return DetailResponse.builder().username(user.get().getUsername()).email(user.get().getEmail()).created_at(user.get().getCreatedAt()).updated_at(user.get().getUpdatedAt()).build();
    }


}
