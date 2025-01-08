package com.sparta.fritown.domain.dto.live;

import lombok.Data;

@Data
public class LiveChatRoomResponse {
    private Channel channel;

    @Data
    public static class Channel {
        private String id;
        // 필요한 경우 다른 필드도 추가
    }
}
