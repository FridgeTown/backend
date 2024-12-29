package com.sparta.fritown.domain.dto.chat.channel.component;

import java.util.Map;

public class LastMessageDto {
    private String id;
    private String channelId;
    private String userId;
    private String username;
    private String profileImageUrl;
    private String type;
    private String text;
    private Map<String, Object> translations;
    private Map<String, Object> data;
    private long createdAt;
}
