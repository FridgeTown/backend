package com.sparta.fritown.domain.dto.chat.channel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserChannelsRequestDto {
    private String userId;       // Path Parameter: 필수
    private String category;     // Query Parameter: 선택
    private String subcategory;  // Query Parameter: 선택
    private String lastChannelId;// Query Parameter: 선택

    public GetUserChannelsRequestDto(String userId) {
        this.userId = userId;
    }
}
