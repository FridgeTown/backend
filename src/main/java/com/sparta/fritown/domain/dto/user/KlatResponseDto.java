package com.sparta.fritown.domain.dto.user;

import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
@Getter
public class KlatResponseDto {
    private UserInfo user;
    private String loginToken;

    @Data
    public static class UserInfo {
        private String id;
        private String username;
        private String profileImageUrl;
        private boolean disablePushNotification;
        private Map<String, String> data;
        private Long updatedAt;
        private Long createdAt;
    }

    public KlatResponseDto(){}
}
