package com.sparta.fritown.domain.dto.punchGame;

import lombok.Getter;

@Getter
public class PunchGameStartResponseDto {

    private Long opponentId;
    private String opponentNickname;
    private String opponentGender;
    private Integer opponentAge;
    private Integer opponentHeight;
    private Integer opponentWeight;
    private String opponentWeightClass;
    private String opponentProfileImg;


    public PunchGameStartResponseDto(
            Long opponentId,
            String opponentNickname,
            String opponentGender,
            Integer opponentAge,
            Integer opponentHeight,
            Integer opponentWeight,
            String opponentWeightClass,
            String opponentProfileImg ) {

        this.opponentId = opponentId;
        this.opponentNickname = opponentNickname;
        this.opponentGender = opponentGender;
        this.opponentAge = opponentAge;
        this.opponentHeight = opponentHeight;
        this.opponentWeight = opponentWeight;
        this.opponentWeightClass = opponentWeightClass;
        this.opponentProfileImg = opponentProfileImg;
    }
}
