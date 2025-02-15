package com.sparta.fritown.domain.dto.live;

import com.sparta.fritown.domain.entity.Matches;
import lombok.Getter;

@Getter
public class LiveResponseDto {
    private final Long matchId;
    private final String title;
    private final String thumbNail;
    private final String place;

    public LiveResponseDto(Matches matche, String fileUrl) {
        this.matchId = matche.getId();
        this.title = matche.getTitle();
        this.thumbNail = fileUrl;
        this.place = matche.getPlace();
    }
}
