package com.sparta.fritown.domain.dto.live;

import lombok.Getter;

@Getter
public class liveResponseDto {
    private final Long matchId;
    private final String title;
    private final String String;
    private final String place;

    public liveResponseDto(Long matchId, java.lang.String title, java.lang.String string, java.lang.String place) {
        this.matchId = matchId;
        this.title = title;
        String = string;
        this.place = place;
    }
}
