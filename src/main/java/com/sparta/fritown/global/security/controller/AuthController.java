package com.sparta.fritown.global.security.controller;

import com.sparta.fritown.domain.dto.user.*;
import com.sparta.fritown.global.docs.AuthControllerDocs;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import com.sparta.fritown.global.security.util.JwtUtil;
import com.sparta.fritown.global.security.repository.RefreshTokenRepository;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    private final UserService userService;
    private final RefreshTokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Value("${klat.id}")
    private String klatId;

    @Value("${klat.key}")
    private String klatKey;

    @Value("${klat.user.password}")
    private String klatUserPassword;


    //LoginRequestDto -> 아마 email 정보, provider, 토큰 정보 들이 포함..?
    @PostMapping("/login")
    public ResponseEntity<StatusResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            // 아이디 토큰을 인증
            Claims claims = jwtUtil.validateIdToken(loginRequestDto.getIdToken(), loginRequestDto.getProvider());

            String email = loginRequestDto.getEmail();
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(StatusResponseDto.addStatus(401));
            }

            String role = user.getRole();
            LoginResponseDto loginResponseDto = jwtUtil.generateToken(email, role);

            String chatToken = callChatLoginApi(user.getId());
            loginResponseDto.setChatToken(chatToken);

            return ResponseEntity.ok(StatusResponseDto.success(loginResponseDto));
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(StatusResponseDto.addStatus(500));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<StatusResponseDto> signup(@RequestBody RegisterRequestDto registerRequestDto) {
        log.info("회원가입 요청 정보: {}", registerRequestDto);

        User user = userService.register(registerRequestDto);
        if (user == null) {
           log.info("유저가 널이야!");
        }

        //채팅 회원 가입
        String chatToken = callChatSignupApi(user.getId(), user.getNickname(), user.getProfileImg());

        LoginRequestDto loginRequestDto = new LoginRequestDto(registerRequestDto);

        // 회원 가입 성공 후, login 시도
        return login(loginRequestDto);
    }

    private String callChatSignupApi(Long longUserId, String username, String profileImageUrl) {

        String userId = String.valueOf(longUserId);
        String externalApiUrl = "https://api.talkplus.io/v1.4/api/users/create";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("app-id", klatId);
            headers.set("api-key", klatKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            KlatCreateUserRequestDto requestDto = new KlatCreateUserRequestDto(userId, username, profileImageUrl, klatUserPassword);

            HttpEntity<KlatCreateUserRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);


            ResponseEntity<KlatResponseDto> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    KlatResponseDto.class
            );

            log.info("response: {}", response);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getLoginToken();
            } else {
                log.error("외부 API 호출 실패. 상태 코드 : {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("외부 API 호출 실패 : {}", e.getMessage());
            return null;
        }
    }

    public String callChatLoginApi(Long longUserId) {
        String userId = String.valueOf(longUserId);
        String externalApiUrl = "https://api.talkplus.io/v1.4/api/users/login";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("app-id", klatId);
            headers.set("api-key", klatKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            KlatLoginRequestDto requestDto = new KlatLoginRequestDto(userId, klatUserPassword);
            HttpEntity<KlatLoginRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

            ResponseEntity<KlatResponseDto> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    KlatResponseDto.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getLoginToken();
            } else {
                log.error("외부 API 호출 실패. 상태 코드 : {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("외부 API 호출 실패 : {}", e.getMessage());
            return null;
        }
    }

}