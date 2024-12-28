package com.sparta.fritown.domain.dto.match;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MatchFutureDto {
    private final Long matchId;
    private final String opponent;
    private final LocalDate date;
    private final String place;

    public MatchFutureDto(Long matchId, String opponent, LocalDate date, String place) {
        this.matchId = matchId;
        this.opponent = opponent;
        this.date = date;
        this.place = place;
    }

}
