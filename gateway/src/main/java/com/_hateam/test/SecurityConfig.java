package com._hateam.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtReactiveAuthenticationManager authenticationManager;
    private final JwtSecurityContextRepository contextRepository;

    public SecurityConfig(JwtReactiveAuthenticationManager authenticationManager,
                          JwtSecurityContextRepository contextRepository) {
        this.authenticationManager = authenticationManager;
        this.contextRepository = contextRepository;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(contextRepository)
                .authorizeExchange(exchange -> exchange
                                .pathMatchers("/api/auth/**").permitAll()
//                        .pathMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/webjars/**").permitAll()
//                                .pathMatchers(HttpMethod.PUT,"/api/users/roles/**").permitAll()
                                .pathMatchers(HttpMethod.PUT,"/api/users/roles/**").hasAuthority("ADMIN")
//                        .pathMatchers("/api/hub/**").hasAuthority("HUB")
//                        .pathMatchers(HttpMethod.GET, "/api/reports/hub/**").hasAnyAuthority("ADMIN", "HUB")
//                        .pathMatchers("/api/delivery/**").hasAuthority("DELIVERY")
//                        .pathMatchers(HttpMethod.GET, "/api/orders/assigned/**").hasAnyAuthority("DELIVERY", "ADMIN")
//                        .pathMatchers(HttpMethod.PUT, "/api/orders/status/**").hasAuthority("DELIVERY")
//                        .pathMatchers("/api/company/**").hasAuthority("COMPANY")
//                        .pathMatchers(HttpMethod.GET, "/api/orders/company/**").hasAnyAuthority("COMPANY", "ADMIN")
//                        .pathMatchers(HttpMethod.POST, "/api/orders/new").hasAuthority("COMPANY")
//                        .pathMatchers(HttpMethod.GET, "/api/common/**").authenticated()
//                        .pathMatchers(HttpMethod.GET, "/api/notices/**").authenticated()
                                .anyExchange().authenticated()//필터에걸리면 인증이안된거니까 통과안되나?
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);

                            return response.setComplete();
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            return response.setComplete();
                        })
                )
                .build();
    }
}