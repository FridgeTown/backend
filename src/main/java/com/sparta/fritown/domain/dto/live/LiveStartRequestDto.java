package com.sparta.fritown.domain.dto.live;

import lombok.Getter;

@Getter
public class LiveStartRequestDto {
    private final Long matchId;
    private final String place;

    public LiveStartRequestDto(Long matchId, String place) {
        this.matchId = matchId;
        this.place = place;
    }
}
