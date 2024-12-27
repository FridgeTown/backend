package com.sparta.fritown.domain.dto.rounds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RoundsDto {
    Integer roundNum;
    Integer kcal;
    Integer heartBeat;

    public RoundsDto(Integer roundNum, Integer kcal, Integer heartBeat) {
        this.roundNum = roundNum;
        this.kcal = kcal;
        this.heartBeat = heartBeat;
    }
}
