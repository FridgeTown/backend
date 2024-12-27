package com.sparta.fritown.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundDto {

    Integer roundNum;
    Integer kcal;
    Integer heartBeat;

}
