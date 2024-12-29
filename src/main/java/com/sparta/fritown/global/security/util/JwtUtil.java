package com.sparta.fritown.global.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fritown.global.security.auth.GeneratedToken;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    // JwtUtil은 JWT를 생성, 검증 및 데이터를 추출하는 유틸리티. (access, refresh token 생성. 토큰 유효성 검증. 토큰에서 정보 추출)
    private final JwtProperties jwtProperties;
    private String secretKey; // 서명하고 검증하는 데 사용되는 비밀 키를 저장.


    private String googleJwksUrl = "https://www.googleapis.com/oauth2/v3/certs"; // Google JWKS URL
    private String appleJwksUrl = "https://appleid.apple.com/auth/keys"; // Apple JWKS URL

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

        String accessToken = generateAccessToken(email, role); // access Token 생성

        // 토큰을 Redis에 저장한다. **
        return new GeneratedToken(accessToken);
    }


    public String generateAccessToken(String email, String role) {
        long tokenPeriod = 1000L * 60L * 60L * 24L * 14; // 2주 : access token 유효 시간.
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

            return expiration != null && expiration.after(new Date());
        } catch (JwtException e) {
            log.error("JWT 파싱 오류 : {}", e.getMessage());
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

    // Id token 검증 시작
    public Claims validateToken(String token, String provider) throws JwtException {
        RSAPublicKey publicKey = getPublicKey(provider);
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Public Key 가져오기
    private RSAPublicKey getPublicKey(String provider) throws JwtException {
        String jwksUrl = provider.equals("google") ? googleJwksUrl : appleJwksUrl;
        Map<String, Object> jwks = fetchJwks(jwksUrl); // 알맞은 provider를 선택하고, fetchJwks를 통해 PublicKey를 가져오기 (provider에서 준 거)
        return getPublicKeyFromJwks(jwks);
    }

    private Map<String, Object> fetchJwks(String jwksUrl) throws JwtException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(jwksUrl, String.class); // restTemplate을 이용하여 GET 요청.
            return parseJWKS(response.getBody()); // response에 json이 들어오게 됨. 이를 parseJWKS를 통해 파싱
        } catch (Exception e) {
            throw new JwtException("Public Key를 가져오거나 분석할 수 없습니다.");
        }
    }

    private Map<String, Object> parseJWKS(String jwksJson) throws JwtException {
        try {
            return new ObjectMapper().readValue(jwksJson, Map.class);
        } catch (IOException e) {
            throw new JwtException("JWKS JSON을 파싱해서 map 형태로 변환하는 데 실패했습니다.", e);
        }
    }

    private RSAPublicKey getPublicKeyFromJwks(Map<String, Object> jwks) throws JwtException {
        // "keys" 항목이 List 형태로 있는지 확인
        Object keysObj = jwks.get("keys");

        // 'keys'가 List<?> 타입인지 확인
        if (keysObj instanceof List<?>) {
            List<?> keys = (List<?>) keysObj;

            // 각 항목이 Map<String, Object>인지 확인
            for (Object keyObj : keys) {
                if (keyObj instanceof Map<?, ?>) {
                    Map<String, Object> key = (Map<String, Object>) keyObj; // 안전하게 캐스팅

                    // 공개 키를 찾는 로직
                    String kid = (String) key.get("kid");
                    if (kid != null) {
                        return parsePublicKey(key);  // RSAPublicKey 객체로 변환
                    }
                } else {
                    throw new JwtException("JWKS의 키 항목 (keyObj) 이 Map 형식이 아닙니다.");
                }
            }
        } else {
            throw new JwtException("JWKS에서 'keys' 항목이 List 형태가 아닙니다.");
        }

        throw new JwtException("Public Key를 jwks에서 찾을 수 없습니다.");
    }

    // 공개 키 파싱
    private RSAPublicKey parsePublicKey(Map<String, Object> key) throws JwtException {
        try {
            // n, e 는 Base64 URL 형식으로 인코딩 되어 있음.
            String n = (String) key.get("n");
            String e = (String) key.get("e");

            // 디코딩하여 RSAPublicKey 생성.
            byte[] modulus = Base64.getUrlDecoder().decode(n);
            byte[] exponent = Base64.getUrlDecoder().decode(e);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // RSA 방식을 이용하는 KeyFactory 함수 생성.
            // modulus, exponent 값을 BigInteger 객체로 변환.
            return (RSAPublicKey) keyFactory.generatePublic(
                    new RSAPublicKeySpec(new BigInteger(1, modulus), new BigInteger(1, exponent)));
        } catch (Exception e) {
            throw new JwtException("공개 키를 분석하는 데 실패했습니다.", e);
        }
    }


}