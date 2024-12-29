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
    /* Public Key : 아이디 토큰 -> kid 값 추출 -> 소셜에서 제공하는 URL 접속을 통해 key 값들을 가지고 있는 JSON 받아오기
     * -> 그 중 kid 값과 일치하는 n, e 값 추출 -> 이를 바탕으로 해당 알고리즘에 맞는 publicKey 생성
     * -> 찾아낸 공개 키를 바탕으로, parseClaimsJws(idToken) 아이디 토큰 검증.
     *
     * Private Key : 서버에서 설정해 둔, 알고리즘(여기에선, HS256)으로 만든 private Key SecretKey를 의미함.
     * 이를 이용해서, generate Token을 통해 access Token을 반환.
     */
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
        // private Key로 accessToken 생성 후 반환.
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
        // 아이디 토큰이 JWT 형식이므로, .으로 분리해서 3 부분으로 분리
        String[] jwtParts = idToken.split("\\.");

        // URL 및 파일 이름에 안전한 Base64 디코더를 이용해서, header를 디코드!
        String headerJson = new String(Base64.getUrlDecoder().decode(jwtParts[0]));

        // Jwt 헤더 부분을 분리해 낸, headerJson을 이용해서, 서명 알고리즘과 토큰 유형을 파악.
        // ex) {"alg":"HS256","typ":"JWT"} 위에서 언급한, googleJwksUrl 여기 url을 타고 가 보면, 더 정확히 알 수 있음.
        Map<String, Object> header = parseJson(headerJson);
        // kid는 Key ID를 나타냄. JWT를 검증할 때, 공개 키를 검정할 때, 이 kid의 value 값을 이용할 수 있음.
        // kid : "" 공개 키를 선택하는 데 사용되는 식별자 "" 이를 통해 JWT 검증 시 사용할 키를 찾을 수 있음.
        String kid = (String) header.get("kid"); // 아이디 토큰에서 kid를 추출해 낸 것.
        log.info("ID Token kid: {}", kid);

        RSAPublicKey publicKey = getPublicKey(provider, kid); // 공개 키가 생성 됨.
        return Jwts.parserBuilder()
                .setSigningKey(publicKey) // keys에서 뽑아낸 n, e 값으로 생성한 공개 키를 세팅
                .build()
                .parseClaimsJws(idToken) // 세팅한 공개 키를 바탕으로 아이디 토큰 검증.
                .getBody();
    }

    private RSAPublicKey getPublicKey(String provider, String kid) throws JwtException {
        String jwksUrl = provider.equals("google") ? googleJwksUrl : appleJwksUrl;
        Map<String, Object> jwks = fetchJwks(jwksUrl); // 소셜별 url에서 요청을 보내서 keys 에 대한 JSON들을 가지고 옴.
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

    // jwks : 키 값들을 가지고 있는 json , kids : 아이디 토큰에서 추출해 낸, JWT 알고리즘 선택할 때 이용할 수 있는 값.
    private RSAPublicKey getPublicKeyFromJwks(Map<String, Object> jwks, String kid) {
        List<Map<String, Object>> keys = (List<Map<String, Object>>) jwks.get("keys");
        for (Map<String, Object> key : keys) {
            if (kid.equals(key.get("kid"))) { // kids와 일치하는 값을 가지고 있는 key로 parsePublicKey를 진행.
                return parsePublicKey(key);
            }
        }
        throw new JwtException("JWKS에 일치하는 kid를 찾을 수 없습니다.");
    }

    private RSAPublicKey parsePublicKey(Map<String, Object> key) {
        try {
            byte[] modulusBytes = Base64.getUrlDecoder().decode((String) key.get("n")); // modulus 값 (크기 결정)
            byte[] exponentBytes = Base64.getUrlDecoder().decode((String) key.get("e")); // exponent 값

            BigInteger modulus = new BigInteger(1, modulusBytes); // BigInteger를 통해 큰 곳에 저장할 수 있도록 하고, 1을 명시를 통해 양의 정수임을 알려줌.
            BigInteger exponent = new BigInteger(1, exponentBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent)); // key에서 뽑아낸, n, e 값으로 RSA 알고리즘에 맞는 공개 키를 생성.
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