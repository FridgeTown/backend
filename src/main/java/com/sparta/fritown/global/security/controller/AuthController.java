package com.sparta.fritown.global.security.controller;

import com.sparta.fritown.domain.dto.user.LoginRequestDto;
import com.sparta.fritown.domain.dto.user.LoginResponseDto;
import com.sparta.fritown.global.docs.AuthControllerDocs;
import com.sparta.fritown.global.security.auth.GeneratedToken;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import com.sparta.fritown.global.security.util.JwtUtil;
import com.sparta.fritown.global.security.repository.RefreshTokenRepository;
import com.sparta.fritown.domain.dto.user.RegisterRequestDto;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    private final UserService userService;
    private final RefreshTokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    //LoginRequestDto -> 아마 email 정보, provider, 토큰 정보 들이 포함..?
    @PostMapping("/login")
    public ResponseEntity<StatusResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Claims claims = jwtUtil.validateIdToken(loginRequestDto.getIdToken(), loginRequestDto.getProvider());

            log.info("Claim 후 로직: {}", claims);
            String email = loginRequestDto.getEmail();
            //log.info("Claims에서 얻은 이메일: {}", email);
            User user = userService.findByEmail(email);
            log.info("findByEmail 후 로직");

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(StatusResponseDto.addStatus(401));
            }

            String role = user.getRole();
            LoginResponseDto loginResponseDto = jwtUtil.generateToken(email, role);

            return ResponseEntity.ok(StatusResponseDto.success(loginResponseDto));
        } catch (JwtException e) {
            log.error("토큰 검증에 실패했습니다 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(StatusResponseDto.addStatus(401));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<StatusResponseDto> signup(@RequestBody RegisterRequestDto registerRequestDto) {
        log.info("회원가입 요청 정보: {}", registerRequestDto);

        User user = userService.register(registerRequestDto);
        if (user == null) {
           log.info("유저가 널이야!");
        }
        LoginRequestDto loginRequestDto = new LoginRequestDto(registerRequestDto);

        return login(loginRequestDto);
    }

}