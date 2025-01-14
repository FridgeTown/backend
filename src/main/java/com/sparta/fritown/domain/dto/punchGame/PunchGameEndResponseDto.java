package com.sparta.fritown.domain.dto.punchGame;

import lombok.Getter;

@Getter
public class PunchGameEndResponseDto {
    private String nickname;
    private int finalPunch;
    private double avgHeartRate;
    private double finalCalorie;

    public PunchGameEndResponseDto(String nickname, int finalPunch, double avgHeartRate, double finalCalorie) {
        this.nickname = nickname;
        this.finalPunch = finalPunch;
        this.avgHeartRate = avgHeartRate;
        this.finalCalorie = finalCalorie;
    }
}
