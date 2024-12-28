package com.sparta.fritown.domain.dto.match;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MatchSummaryDto {
    private final Long matchId;
    private final String opponent;
    private final Integer roundNum;
    private final Integer kcal;
    private final LocalDate date;
    private final Integer heartBeat;
    private final Integer punchCnt;

    public MatchSummaryDto(Long matchId, MatchInfoDto userMatch, String opponent) {
        this.matchId = matchId;
        this.opponent = opponent;
        this.roundNum = userMatch.getRoundNum();
        this.kcal = userMatch.getKcal();
        this.date = userMatch.getDate();
        this.heartBeat = userMatch.getHeartBeat();
        this.punchCnt = userMatch.getPunchSum();
    }
}
