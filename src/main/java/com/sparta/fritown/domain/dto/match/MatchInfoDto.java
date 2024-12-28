package com.sparta.fritown.domain.dto.match;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MatchInfoDto {
    private final Integer roundNum;
    private final Integer kcal;
    private final LocalDate date;
    private final Integer heartBeat;
    private final Integer punchSum;

    public MatchInfoDto(Integer totalKcal, Integer avgHeartBeat, Integer totalPunchNum, Integer roundNums, LocalDate date) {
        this.roundNum = roundNums;
        this.kcal = totalKcal;
        this.date = date;
        this.heartBeat = avgHeartBeat;
        this.punchSum = totalPunchNum;
    }
}
