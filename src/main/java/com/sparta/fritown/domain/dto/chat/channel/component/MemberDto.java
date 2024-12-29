package com.sparta.fritown.domain.dto.chat.channel.component;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MemberDto {
    private String id;
    private String username;
    private String profileImageUrl;
    private Map<String, Object> data;
    private Map<String, Object> memberInfo;
    private long lastReadAt;
    private long lastSentAt;
    private long updatedAt;
    private long createdAt;
}
