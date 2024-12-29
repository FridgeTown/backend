package com.sparta.fritown.domain.dto.chat.user.component;

import lombok.Getter;

import java.util.Map;

@Getter
public class ChatUserDto {
    private String id;
    private String username;
    private String profileImageUrl;
    private boolean disablePushNotification;
    private Map<String, Object> data;
    private long updatedAt;
    private long createdAt;
}

