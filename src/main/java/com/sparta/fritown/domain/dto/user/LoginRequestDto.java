package com.sparta.fritown.domain.dto.user;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String email;
    private String provider;
    private String idToken;

    public LoginRequestDto() {

    }
    public LoginRequestDto(RegisterRequestDto registerRequestDto) {
        this.email = registerRequestDto.getEmail();
        this.provider = registerRequestDto.getProvider();
        this.idToken = registerRequestDto.getIdToken();
    }
}
