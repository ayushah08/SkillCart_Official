package com.skillcart.skillcart_auth_service.controller;

import com.skillcart.skillcart_auth_service.dto.DetailResponse;
import com.skillcart.skillcart_auth_service.service.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController("/v1/user/details")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public DetailResponse getDetails() {

        return userProfileService.getUserDetails();
    }
}
