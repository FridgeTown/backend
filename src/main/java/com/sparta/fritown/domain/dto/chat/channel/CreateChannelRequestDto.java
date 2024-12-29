package com.sparta.fritown.domain.dto.chat.channel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CreateChannelRequestDto {
    private String name;                  // 채널명 (선택 필드)
    private String ownerId;               // 채널 소유자 ID (선택 필드)
    private String type;                  // 채널 타입 (필수 필드)
    private String channelId;             // 채널 ID (선택 필드)
    private Boolean reuseChannel;         // 채널 재사용 여부 (선택 필드)
    private String invitationCode;        // 초대 코드 (invitationOnly일 경우 필수)
    private List<String> members;         // 멤버 ID 리스트 (선택 필드)
    private String imageUrl;              // 채널 이미지 URL (선택 필드)
    private Map<String, Object> data;     // 메타 정보 (선택 필드)
    private Integer maxMemberCount;       // 최대 멤버 수 (선택 필드)
    private Boolean hideMessagesBeforeJoin; // 가입 이전 메시지 숨김 여부 (선택 필드)
    private String category;              // 카테고리 (선택 필드)
    private String subcategory;           // 서브 카테고리 (선택 필드)

    public CreateChannelRequestDto() {} // 기본 생성자

    public CreateChannelRequestDto(String name, String ownerId, String type, String channelId, Boolean reuseChannel, String invitationCode, List<String> members, String imageUrl, Map<String, Object> data, Integer maxMemberCount, Boolean hideMessagesBeforeJoin, String category, String subcategory) {

        this.name = name;
        this.ownerId = ownerId;
        this.type = type;
        this.channelId = channelId;
        this.reuseChannel = reuseChannel;
        this.invitationCode = invitationCode;
        this.members = members;
        this.imageUrl = imageUrl;
        this.data = data;
        this.maxMemberCount = maxMemberCount;
        this.hideMessagesBeforeJoin = hideMessagesBeforeJoin;
        this.category = category;
        this.subcategory = subcategory;
    }
}
