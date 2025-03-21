//package com._hateam.filter;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
////import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.*;
//import java.util.function.Function;
//
//@Component
//public class JwtUtil {
//
////    @Value("${service.jwt.secret-key}")
////    private String secretKey;
////
////    @Value("${spring.jwt.accessTokenValidityInMilliseconds}")
////    private long jwtExpiration;
////
////    @Value("${spring.jwt.refreshTokenValidityInMilliseconds}")
////    private long refreshTokenExpiration;
////
////
////    @PostConstruct
////    protected void init() {
////        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
////    }
//
////    // 토큰에서 사용자 ID 추출
////    public Long extractUserId(String token) {
////        Claims claims = getClaims(token);
////        // claims에서 "userId" 필드를 추출 (토큰 생성 시 이 필드를 넣어줘야 함)
////        return Long.parseLong(claims.get("userId", String.class));
////    }
////
////    // 토큰에서 사용자 역할 추출
////    public String extractUserRole(String token) {
////        Claims claims = getClaims(token);
////        // "roles" 필드가 List<String> 타입으로 저장되어 있다고 가정
////        @SuppressWarnings("unchecked")
////        List<String> roles = (List<String>) claims.get("roles");
////        // 간단하게 첫 번째 역할만 반환 (다수의 역할이 있을 경우 별도 처리 필요)
////        return roles != null && !roles.isEmpty() ? roles.get(0) : "";
////    }
////
////    // 토큰의 유효성 + 만료일자 확인
////    public boolean validateToken(String token) {
////        try {
////            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
////            return !claims.getBody().getExpiration().before(new Date());
////        } catch (JwtException | IllegalArgumentException e) {
////            return false;
////        }
////    }
////
////    // 토큰에서 클레임 추출
////    private Claims getClaims(String token) {
////        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
////    }
//}
