package com._hateam.user.infrastructure.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component

public class JwtUtil {

    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.accessTokenValidityInMilliseconds}")
    private long jwtExpiration;

    @Value("${spring.jwt.refreshTokenValidityInMilliseconds}")
    private long refreshTokenExpiration;

    // 액세스 토큰 생성
    public String generateAccessToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("tokenType", "access");
        //return createToken(claims, username, jwtExpiration);
        return createToken(claims, jwtExpiration);
    }

    // 리프레시 토큰생성
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tokenType", "refresh");
        return createToken(claims, refreshTokenExpiration);
    }

    //토큰생성 메서드
    private String createToken(Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰검증
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        // 토큰에서 userId 추출
//        final Long userId = extractUserId(token);
//
//        // UserDetails가 UserPrincipal 타입인지 확인하고 ID 일치 여부 검증
//        if (userDetails instanceof UserPrincipals) {
//            return (userId.equals(((UserPrincipals) userDetails).getId()) && !isTokenExpired(token));
//        }
//        return false;
//    }
    // 이름 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 만료된 날짜 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 유저 아이디 추출
    public Long extractUserId(String token) {
      //  System.out.println("추출완료창근"+extractAllClaims(token).get("userId", Long.class));
        return extractAllClaims(token).get("userId", Long.class);
    }

    // 역할 추출
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // 토큰에서 클레임 값 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 토큰 타입 추출
    public String extractTokenType(String token) {
        return extractAllClaims(token).get("tokenType", String.class);
    }

    // 토큰에서 모든 클레임 값 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 만료 여부 체크
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 서명 키 가져오기
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
