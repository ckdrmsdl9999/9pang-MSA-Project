package com._hateam.common.config;

import com._hateam.common.resolver.UserHeaderInfoArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CommonWebConfig implements WebMvcConfigurer {

    @Autowired
    private UserHeaderInfoArgumentResolver userHeaderInfoArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userHeaderInfoArgumentResolver);
    }
}