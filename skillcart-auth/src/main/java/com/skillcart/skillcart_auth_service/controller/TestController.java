package com.skillcart.skillcart_auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/authentication/test")
    public String test() {
        return "Authenticated Successfully";
    }

    @GetMapping("/public/test")
    public String publicTest() {
        return "Public Test Successfully";
    }

}
