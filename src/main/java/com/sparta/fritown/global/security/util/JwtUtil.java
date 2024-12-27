package com.sparta.fritown.global.security.util;

import com.sparta.fritown.global.security.auth.GeneratedToken;
import com.sparta.fritown.global.security.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUtil {
    // JwtUtil은 JWT를 생성, 검증 및 데이터를 추출하는 유틸리티. (access, refresh token 생성. 토큰 유효성 검증. 토큰에서 정보 추출)
    private final JwtProperties jwtProperties;
    private final RefreshTokenService tokenService;
    private String secretKey; // 서명하고 검증하는 데 사용되는 비밀 키를 저장.

    @PostConstruct
    protected void init() {
//        secretKey = jwtProperties.getSecret();
//        log.info("비밀 키 initialized: {}", secretKey);
        secretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecret().getBytes());
        log.info("Base64로 인코딩된 비밀 키: {}", secretKey);
    }


    public GeneratedToken generateToken(String email, String role) {
        // refreshToken과 accessToken을 생성한다.
        log.info("이메일을 위한 토큰 발행: {}", email);
        log.info("비밀 키를 사용 : {}", secretKey);

        String refreshToken = generateRefreshToken(email, role); // refresh Token 생성
        String accessToken = generateAccessToken(email, role); // access Token 생성

        // 토큰을 Redis에 저장한다. **
        tokenService.saveTokenInfo(email, refreshToken, accessToken); // email, refreshToken, accessToken 값을 redis에 저장
        return new GeneratedToken(accessToken, refreshToken);
    }

    public String generateRefreshToken(String email, String role) {
        // 토큰의 유효 기간을 밀리초 단위로 설정.
        long refreshPeriod = 1000L * 60L * 60L * 24L * 14; // 2주동안 refresh token이 유효하도록 함.

        // 새로운 클레임 객체를 생성하고, 이메일과 역할(권한)을 셋팅
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        // 현재 시간과 날짜를 가져온다.
        Date now = new Date();

        return Jwts.builder()
                // Payload를 구성하는 속성들을 정의한다.
                .setClaims(claims)
                // 발행일자를 넣는다.
                .setIssuedAt(now)
                // 토큰의 만료일시를 설정한다.
                .setExpiration(new Date(now.getTime() + refreshPeriod))
                // 지정된 서명 알고리즘과 비밀 키를 사용하여 토큰을 서명한다.
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    } // refresh token 반환


    public String generateAccessToken(String email, String role) {
        long tokenPeriod = 1000L * 60L * 30L; // 30분 : access token 유효 시간.
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        return
                Jwts.builder()
                        // Payload를 구성하는 속성들을 정의한다.
                        .setClaims(claims)
                        // 발행일자를 넣는다.
                        .setIssuedAt(now)
                        // 토큰의 만료일시를 설정한다.
                        .setExpiration(new Date(now.getTime() + tokenPeriod))
                        // 지정된 서명 알고리즘과 비밀 키를 사용하여 토큰을 서명한다.
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .compact();

    }


    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey) // 비밀키를 설정하여 파싱한다.
                    .parseClaimsJws(token);  // 주어진 토큰을 파싱하여 Claims 객체를 얻는다.
            // 토큰의 만료 시간과 현재 시간비교
            Date expiration = claims.getBody().getExpiration();
            log.info("토큰 만료 시간: {}", expiration);

            if (expiration != null) {
                return expiration.after(new Date());
            } else {
                log.error("토큰에 만료 시간 정보가 없습니다.");
                return false;
            }

        } catch (JwtException e) {
            log.error("JWT 파싱 오류 : {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage());
            return false;
        }
    }


    // 토큰에서 Email을 추출한다.
    public String getUid(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject(); // subject가 이메일에 해당
    }

    // 토큰에서 ROLE(권한)만 추출한다.
    public String getRole(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("role", String.class);
    }

}