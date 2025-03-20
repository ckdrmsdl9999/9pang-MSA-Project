package com._hateam.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class jwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // JWT 토큰 검사를 제외할 경로
    private static final List<String> excludeUrls = List.of(
            "/api/auth/signin"
            // 필요시 다른 제외 경로 추가
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 요청 경로 확인
        String path = request.getPath().value();

        // 제외 경로인 경우 필터 스킵
        if (shouldSkip(path)) {
            return chain.filter(exchange);
        }

        // Authorization 헤더에서 JWT 토큰 추출
        List<String> authHeaders = request.getHeaders().get("Authorization");

        // 토큰이 없는 경우 처리
        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.just(
                    response.bufferFactory().wrap("JWT 토큰이 필요합니다. Authorization: Bearer {token} 형식으로 요청하세요.".getBytes())
            ));
        }

        // Bearer 접두사 제거
        String jwt = authHeaders.get(0).substring(7);

        try {
            // JWT 토큰에서 사용자 ID와 역할 추출
            Long userId = jwtUtil.extractUserId(jwt);
            String userRole = jwtUtil.extractUserRole(jwt);

            // 추출한 정보를 요청 헤더에 추가
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Role", userRole)
                    .build();

            // 수정된 요청으로 다음 필터 호출
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            // 토큰 검증 실패시 401 응답
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.just(
                    response.bufferFactory().wrap("유효하지 않은 JWT 토큰입니다.".getBytes())
            ));
        }
    }

    // 제외 경로 확인 메소드
    private boolean shouldSkip(String path) {
        return excludeUrls.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

//    // 필터 실행 순서 (낮은 값이 높은 우선순위)
//    @Override
//    public int getOrder() {
//        return -100;
//    }

}
