package com._hateam.config;

import com._hateam.filter.JwtReactiveAuthenticationManager;
import com._hateam.filter.JwtSecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import reactor.core.publisher.Mono;
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
                                .pathMatchers("/api/auth/**").permitAll()//인가목록 여기에 추가
                                .pathMatchers(HttpMethod.PUT,"/api/users/roles/**").hasAuthority("ADMIN")

//                        .pathMatchers("/api/company/**").hasAuthority("COMPANY")
//                        .pathMatchers(HttpMethod.GET, "/api/orders/company/**").hasAnyAuthority("COMPANY", "ADMIN")
//                        .pathMatchers(HttpMethod.POST, "/api/orders/**").hasAuthority("COMPANY")
//                        .pathMatchers(HttpMethod.POST, "/api/deliver/**").hasAuthority("COMPANY")

                                .anyExchange().authenticated()//
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            String errorMessage = "{\"message\":\"적절하지 않은 사용자입니다.\"}";
                            DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
                            return response.writeWith(Mono.just(buffer));
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                            String errorMessage = "{\"message\":\"적절한 권한이 아닙니다.\"}";
                            DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));

                            return response.writeWith(Mono.just(buffer));
                        })
                )
                .build();
    }
}