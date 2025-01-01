package com.sparta.fritown.domain.dto.user;

import lombok.Getter;
import org.springframework.core.io.Resource;

import java.util.Map;

@Getter
public class KlatImageRequestDto {
    private String username;
    private String profileImageUrl;
    private Map<String, Object> data;


    public KlatImageRequestDto(String nickname, String profileImageUrl) {
        this.username = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
