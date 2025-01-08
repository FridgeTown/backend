package com.sparta.fritown.domain.dto.live;

import lombok.Getter;

@Getter
public class LiveStartRequestDto {
    private final String channelId; // 1:1 채팅 아이디
    private final String place;

    public LiveStartRequestDto(String place, String channelId) {
        this.channelId = channelId;
        this.place = place;
    }
}
