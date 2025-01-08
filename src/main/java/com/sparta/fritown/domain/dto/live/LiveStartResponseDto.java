package com.sparta.fritown.domain.dto.live;

import lombok.Getter;

@Getter
public class LiveStartResponseDto {
    private final String chatRoomId;

    public LiveStartResponseDto(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
