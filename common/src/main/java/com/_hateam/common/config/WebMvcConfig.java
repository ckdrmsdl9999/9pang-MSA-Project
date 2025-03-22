package com._hateam.common.config;

import com._hateam.common.resolver.UserHeaderInfoArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public UserHeaderInfoArgumentResolver userHeaderInfoArgumentResolver() {
        return new UserHeaderInfoArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userHeaderInfoArgumentResolver());
        System.out.println("UserHeaderInfoArgumentResolver가 등록되었습니다.");
    }
}