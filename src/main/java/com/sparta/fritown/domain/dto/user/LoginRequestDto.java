package com.sparta.fritown.domain.dto.user;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String email;
    private String token;
}
