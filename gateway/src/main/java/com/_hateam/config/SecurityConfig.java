package com._hateam.config;

import com._hateam.filter.JwtReactiveAuthenticationManager;
import com._hateam.filter.JwtSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import reactor.core.publisher.Mono;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.server.reactive.ServerHttpRequest;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    private final JwtReactiveAuthenticationManager authenticationManager;
    private final JwtSecurityContextRepository contextRepository;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(contextRepository)

                // 헤더 추가 필터 (로깅 추가)
//                .addFilterAfter((exchange, chain) -> {
//                    String path = exchange.getRequest().getURI().getPath();
//                    log.info("===== 헤더 추가 필터 실행: {} =====", path);
//
//                    // 기존 헤더 로깅
//                    log.info("기존 요청 헤더:");
//                    exchange.getRequest().getHeaders().forEach((name, values) -> {
//                        log.info("  {} = {}", name, values);
//                    });
//
//                    return exchange.getPrincipal()
//                            .cast(Authentication.class)
//                            .flatMap(auth -> {
//                                // 인증 객체에서 사용자 ID와 역할 추출
//                                String userId = auth.getPrincipal().toString();
//                                String role = auth.getAuthorities().stream()
//                                        .findFirst()
//                                        .map(GrantedAuthority::getAuthority)
//                                        .orElse("");
//
//                                log.info("인증 정보 추출 완료 - userId: {}, role: {}", userId, role);
//
//                                // 요청 헤더에 사용자 ID와 역할 추가
//                                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                                        .header("x-user-id", userId)
//                                        .header("x-user-role", role)
//                                        .build();
//
//                                // 수정된 헤더 로깅
//                                log.info("수정된 요청 헤더:");
//                                mutatedRequest.getHeaders().forEach((name, values) -> {
//                                    log.info("  {} = {}", name, values);
//                                });
//
//                                log.info("x-user-id 헤더 확인: {}", mutatedRequest.getHeaders().getFirst("x-user-id"));
//                                log.info("x-user-role 헤더 확인: {}", mutatedRequest.getHeaders().getFirst("x-user-role"));
//
//                                // 수정된 요청으로 교체하고 필터 체인 계속 진행
//                                return chain.filter(exchange.mutate()
//                                        .request(mutatedRequest)
//                                        .build());
//                            })
//                            .switchIfEmpty(Mono.defer(() -> {
//                                log.warn("인증 정보가 없음 - 헤더를 추가하지 않고 계속 진행");
//                                return chain.filter(exchange);
//                            }));
//                }, SecurityWebFiltersOrder.AUTHENTICATION)

                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/api/users/signup").permitAll()
                        .pathMatchers("/api/users/signin").permitAll()
                        .pathMatchers("/hubs/**").permitAll()
                        .pathMatchers("/companies/**").permitAll()
                        .pathMatchers(HttpMethod.PUT,"/api/users/roles/**").hasAuthority("ADMIN")
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            String errorMessage = "{\"message\":\"적절하지 않은 토큰입니다.\"}";
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
