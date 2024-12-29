package com.sparta.fritown.global.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fritown.domain.dto.user.LoginResponseDto;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.security.auth.GeneratedToken;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUtil {
    private String secretKey; // 비밀 키 저장

    @Value("${jwt.secret.key.access}")
    private String secretKeyFromConfig;
    private final UserRepository userRepository;

    @PostConstruct
    protected void init() {
        // Base64로 인코딩된 secretKey 설정
        this.secretKey = Base64.getEncoder().encodeToString(secretKeyFromConfig.getBytes());
        log.info("Secret Key 초기화 완료");
    }
    private final String googleJwksUrl = "https://www.googleapis.com/oauth2/v3/certs"; // Google JWKS URL
    private final String appleJwksUrl = "https://appleid.apple.com/auth/keys"; // Apple JWKS URL

    public LoginResponseDto generateToken(String email, String role) {
        long tokenPeriod = 1000L * 60L * 60L * 24L * 7; // 7일 유효기간
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        User user = userRepository.findByEmail(email).orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));
        LoginResponseDto loginResponseDto = new LoginResponseDto(user, token);

        return loginResponseDto;
    }

    public Claims validateIdToken(String idToken, String provider) throws JwtException {
        log.info("validateIdToken 시작");

        String[] jwtParts = idToken.split("\\.");
        String headerJson = new String(Base64.getUrlDecoder().decode(jwtParts[0]));
        log.info("JWT Header: {}", headerJson);

        Map<String, Object> header = parseJson(headerJson);
        String kid = (String) header.get("kid");
        log.info("ID Token kid: {}", kid);

        RSAPublicKey publicKey = getPublicKey(provider, kid);
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(idToken)
                .getBody();
    }

    private RSAPublicKey getPublicKey(String provider, String kid) throws JwtException {
        String jwksUrl = provider.equals("google") ? googleJwksUrl : appleJwksUrl;
        Map<String, Object> jwks = fetchJwks(jwksUrl);
        return getPublicKeyFromJwks(jwks, kid);
    }

    private Map<String, Object> fetchJwks(String jwksUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String jwksResponse = restTemplate.getForObject(jwksUrl, String.class);
            return parseJson(jwksResponse);
        } catch (Exception e) {
            throw new JwtException("JWKS 가져오기 실패: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> parseJson(String json) {
        try {
            return new ObjectMapper().readValue(json, Map.class);
        } catch (IOException e) {
            throw new JwtException("JSON 파싱 실패: " + e.getMessage(), e);
        }
    }

    private RSAPublicKey getPublicKeyFromJwks(Map<String, Object> jwks, String kid) {
        List<Map<String, Object>> keys = (List<Map<String, Object>>) jwks.get("keys");
        for (Map<String, Object> key : keys) {
            if (kid.equals(key.get("kid"))) {
                return parsePublicKey(key);
            }
        }
        throw new JwtException("JWKS에 일치하는 kid를 찾을 수 없습니다.");
    }

    private RSAPublicKey parsePublicKey(Map<String, Object> key) {
        try {
            byte[] modulusBytes = Base64.getUrlDecoder().decode((String) key.get("n"));
            byte[] exponentBytes = Base64.getUrlDecoder().decode((String) key.get("e"));

            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (Exception e) {
            throw new JwtException("공개 키 파싱 실패: " + e.getMessage(), e);
        }
    }

    public boolean verifyToken(String token) {
        try {
            // 토큰 서명 검증 및 만료 여부 확인
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 비밀키로 서명 검증
                    .build()
                    .parseClaimsJws(token); // Claims 객체로 파싱

            return true; // 유효한 토큰
        } catch (JwtException e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false; // 유효하지 않은 토큰
        }
    }

    public String getUid(String token) {
        try {
            // 토큰의 클레임에서 subject 값을 추출
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // subject 값 반환
        } catch (JwtException e) {
            log.error("토큰에서 UID 추출 실패: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }
}