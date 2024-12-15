package com.sparta.fritown.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecurityUserDto {
    private Long id;
    private String email;
    private String role;
    private String nickname;
}