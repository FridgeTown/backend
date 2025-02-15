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

    public KlatCreateUserRequestDto(){}
    public KlatCreateUserRequestDto(String userId, String username, String profileImageUrl, String password) {
        this.userId = userId;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.password = password;
    }
}
