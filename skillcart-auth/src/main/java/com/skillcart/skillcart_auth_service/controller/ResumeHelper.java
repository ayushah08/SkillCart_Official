package com.skillcart.skillcart_auth_service.controller;

import com.skillcart.skillcart_auth_service.dto.ResumeResponse;
import com.skillcart.skillcart_auth_service.service.UserForResErvice;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/resume")
public class ResumeHelper {

    private final UserForResErvice service;


    @GetMapping("/getUUID")
    public ResumeResponse getUUID(){
        return service.getUserId();
    }
}
