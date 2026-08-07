package com.skillcart.skillcart_auth_service.service.impl;

import com.skillcart.skillcart_auth_service.dto.ResumeResponse;
import com.skillcart.skillcart_auth_service.repository.UserRepository;
import com.skillcart.skillcart_auth_service.service.UserForResErvice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserForResErviceImpl implements UserForResErvice {

    private final UserRepository userRepository;


    @Override
    public ResumeResponse getUserId() {
        return userRepository.getUserId();
    }
}
