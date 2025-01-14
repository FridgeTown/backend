package com.sparta.fritown.domain.dto.punchGame;

import lombok.Getter;

@Getter
public class PunchGameEndResponseDto {
    private String nickname;
    private int finalPunch;
    private double avgHeartRate;
    private int finalCalorie;
}
