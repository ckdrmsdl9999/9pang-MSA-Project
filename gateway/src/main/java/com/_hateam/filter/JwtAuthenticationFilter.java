package com._hateam.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // 가장 높은 우선순위 설정
//        return Ordered.LOWEST_PRECEDENCE; // 또는 다른 적절한 값it st
    }
    @Value("${service.jwt.secret-key}")
    private String secretKey;

    // JWT 토큰 검증을 제외할 경로 목록
    private static final List<String> EXCLUDE_PATHS = List.of(
            "/api/auth/signIn",
          "/api/users/signup"
              // 필요에 따라 추가/제거 가능
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("===============================================");
        log.info("JWT 필터가 요청경로처리: {}", path);

        // 인증이 필요하지 않은 경로인지 확인
        if (shouldExcludePath(path)) {
            log.info("JWT 검증에서 제외된 경로: {}", path);
            return chain.filter(exchange);
        }

        log.info("\"JWT 검증이 필요한 경로: {}", path);
        // 요청 헤더 로깅
        exchange.getRequest().getHeaders().forEach((name, values) -> {
            log.info("헤더: {} = {}", name, values);
        });

        String token = extractToken(exchange);
        log.info("토큰 추출: {}", token != null ? "존재" : "존재하지않음");

        if (token == null) {
            log.warn("경로 {}에 대한 요청 헤더에서 JWT 토큰을 찾을 수 없습니다: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 토큰 검증 및 클레임 추출
        Claims claims = validateTokenAndGetClaims(token);

        if (claims == null) {
            log.warn("경로 {}에 대한 JWT 토큰이 유효하지 않습니다: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 클레임에서 ID와 역할 추출
        Long userId = claims.get("userId", Long.class);
        String userRole = claims.get("role", String.class);

        log.info("추출된 사용자 ID: {}, 역할: {}", userId, userRole);
        System.out.println(userId+userRole+"롤");
        // 요청 헤더에 사용자 정보 추가 (userId를 문자열로 변환)
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("x-user-id", userId != null ? userId.toString() : "")
                .header("x-user-role", userRole != null ? userRole : "")
                .build();

        // 새 요청으로 교환 객체 업데이트
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.info("JWT 토큰이 성공적으로 검증되었으며, 경로 {}에 대한 헤더에 사용자 정보가 추가되었습니다: {}", path);
        return chain.filter(mutatedExchange);
    }



    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Claims validateTokenAndGetClaims(String token) {
        try {
            // 1. 비밀 키 사용
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            // 2. 토큰 검증
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build().parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();
            log.info("토큰 페이로드: {}", claims);

            // 3. 만료시간 검증
            Date now = new Date();
            Date expiration = claims.getExpiration();

            if (expiration != null && expiration.before(now)) {
                log.warn("토큰이 {}에 만료되었습니다, 현재 시간: {}", expiration, now);
                return null;
            }

            log.info("토큰이 성공적으로 검증되었습니다! 토큰은 {}에 만료됩니다", expiration);
            return claims;
        } catch (Exception e) {
            log.error("토큰 검증이 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    private boolean shouldExcludePath(String requestPath) {
        boolean isExcluded = EXCLUDE_PATHS.stream()
                .anyMatch(pattern -> {
                    boolean matches = pathMatcher.match(pattern, requestPath);
                    if (matches) {
                        log.info("경로 {}가 제외된 패턴 {}와 일치합니다", requestPath, pattern);
                    }
                    return matches;
                });
        return isExcluded;
    }


@PostConstruct
public void init() {
    // Base64 인코딩을 제거하고 원래 키 사용
    log.info("JWT 인증 필터가 비밀 키로 초기화되었습니다: {}",
            secretKey != null ? (secretKey.substring(0, 10) + "...") : "Missing");
}
}
