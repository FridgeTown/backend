package com.sparta.fritown.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private String email;
    private String provider;
    private String name;
    private String profileImage;
    private String role;

    public RegisterRequestDto(String email, String provider, String defaultName, String role) {
        this.email = email;
        this.provider = provider;
        this.name = defaultName;
        this.role = role; // default role
    }
}
