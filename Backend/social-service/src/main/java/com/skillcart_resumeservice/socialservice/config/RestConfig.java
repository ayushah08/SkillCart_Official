package com.skillcart_resumeservice.socialservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    public RestClient restTemplate() {

        return RestClient.create();
    }
}
