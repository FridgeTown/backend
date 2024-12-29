package com.sparta.fritown.domain.dto.user;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String provider;
    private String idToken;

    public LoginRequestDto(RegisterRequestDto registerRequestDto) {
        this.provider = registerRequestDto.getProvider();
        this.idToken = registerRequestDto.getIdToken();
    }
}
