package com.sparta.fritown.domain.dto;

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
