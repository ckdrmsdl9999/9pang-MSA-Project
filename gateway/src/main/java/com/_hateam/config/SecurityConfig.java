//package com._hateam.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // 보안 기능 모두 비활성화 (모든 요청 허용)
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .logout(AbstractHttpConfigurer::disable)
//                .anonymous(anonymous -> anonymous.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                );
//        return http.build();
//    }
//}
