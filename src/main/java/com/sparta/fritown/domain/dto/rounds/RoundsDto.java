package com.sparta.fritown.domain.dto.rounds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundsDto {
    Integer roundNum;
    Integer kcal;
    Integer heartBeat;
}
