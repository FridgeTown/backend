package com.sparta.fritown.global.security.service;

import com.sparta.fritown.global.security.repository.RefreshTokenRepository;
import com.sparta.fritown.global.security.auth.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Transactional
    public void saveTokenInfo(String email, String refreshToken, String accessToken) {
        log.info("saveTokenInfo called with email={}, accessToken={}, refreshToken={}", email, accessToken, refreshToken);
        repository.save(new RefreshToken(email, accessToken, refreshToken)); // RefreshToken이라는 틀에 accessToken, refreshToken 저장 후, refreshToken을 redis에 저장..
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {
        RefreshToken token = repository.findByAccessToken(accessToken)
                .orElseThrow(IllegalArgumentException::new);

        repository.delete(token);
    }
}