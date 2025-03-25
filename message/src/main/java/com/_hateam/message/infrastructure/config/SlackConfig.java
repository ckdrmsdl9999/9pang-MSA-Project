package com._hateam.message.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SlackConfig {

    @Value("${slack.token}")
    private String token;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}