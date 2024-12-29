package com.sparta.fritown.domain.dto.chat.channel.component;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChannelDto {
    private String id;
    private String name;
    private String ownerId;
    private String type;
    private String imageUrl;
    private String invitationCode;
    private boolean isFrozen;
    private boolean hideMessagesBeforeJoin;
    private String category;
    private String subcategory;
    private String privateTag;
    private Map<String, Object> privateData;
    private int memberCount;
    private int maxMemberCount;
    private Map<String, Object> data;
    private List<MemberDto> members;
    private List<MemberDto> mutedUsers;
    private List<MemberDto> bannedUsers;
    private List<MemberDto> bots;
    private long updatedAt;
    private long createdAt;
    private int unreadCount;
    private long lastReadAt;
    private LastMessageDto lastMessage;
}
