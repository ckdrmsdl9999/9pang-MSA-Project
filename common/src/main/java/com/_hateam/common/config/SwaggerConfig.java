package com._hateam.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title(applicationName + " API")
                .description("MSA 기반 물류 관리 및 배송 시스템의 " + applicationName + " API 문서입니다.")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("9해조")
                        .url("https://github.com/9haTeam"))
                .license(new License()
                        .name("Creative Commons Attribution-NonCommercial 4.0 International")
                        .url("https://creativecommons.org/licenses/by-nc/4.0/"));

        // 보안 스키마 설정 (JWT 인증)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // 서버 설정
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Server");

        Server devServer = new Server()
                .url("http://dev.9hateam.com")
                .description("Development Server");

        return new OpenAPI()
                .info(info)
                .servers(Arrays.asList(localServer, devServer))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}