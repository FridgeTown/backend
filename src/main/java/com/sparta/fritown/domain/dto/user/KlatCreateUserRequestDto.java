package com.sparta.fritown.domain.dto.user;

import lombok.*;

import java.util.Map;

@Data
@Getter
public class KlatCreateUserRequestDto {
    private String userId;
    private String password;
    private String username;
    private String profileImageUrl;
    private Map<String, Object> data;

    public KlatCreateUserRequestDto() {}


    public KlatCreateUserRequestDto(String userId, String username, String profileImageUrl) {
        this.userId = userId;
        this.password = "secret";
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }
}
