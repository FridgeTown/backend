package com.sparta.fritown.global.security.controller;

import com.sparta.fritown.domain.dto.user.*;
import com.sparta.fritown.domain.service.KlatService;
import com.sparta.fritown.global.docs.AuthControllerDocs;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import com.sparta.fritown.global.security.util.JwtUtil;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final KlatService klatService;


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

            String chatToken = klatService.login(user.getId());
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
        String chatToken = klatService.signup(user.getId(), user.getNickname(), user.getProfileImg());

        LoginRequestDto loginRequestDto = new LoginRequestDto(registerRequestDto);

        // 회원 가입 성공 후, login 시도
        return login(loginRequestDto);
    }

}