package com.sparta.fritown.global.security.controller;

import com.sparta.fritown.domain.dto.user.LoginRequestDto;
import com.sparta.fritown.global.security.auth.GeneratedToken;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import com.sparta.fritown.global.security.util.JwtUtil;
import com.sparta.fritown.global.security.repository.RefreshTokenRepository;
import com.sparta.fritown.domain.dto.user.RegisterRequestDto;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RefreshTokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    //LoginRequestDto -> 아마 email 정보, provider, 토큰 정보 들이 포함..?
    @PostMapping("/login")
    public ResponseEntity<StatusResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        User user = userService.findByEmail(loginRequestDto.getEmail());

        String role = user.getRole();
        GeneratedToken token = jwtUtil.generateToken(loginRequestDto.getEmail(), role);

        return ResponseEntity.ok(StatusResponseDto.success(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<StatusResponseDto> signup(@RequestBody RegisterRequestDto registerRequestDto) {
        User user = userService.register(registerRequestDto);

        return ResponseEntity.ok(StatusResponseDto.success("성공적으로 가입하였습니다."));
    }


}