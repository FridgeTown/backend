package com.sparta.fritown.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseStatus {
    private int status;       // HTTP 상태 코드
    private String accessToken; // 새로 발급된 액세스 토큰

    // 상태와 토큰을 설정하는 메서드
    public static TokenResponseStatus addStatus(int status, String accessToken) {
        return new TokenResponseStatus(status, accessToken);
    }
}