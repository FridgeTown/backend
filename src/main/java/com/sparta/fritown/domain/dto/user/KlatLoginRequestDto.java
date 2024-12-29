package com.sparta.fritown.domain.dto.user;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class KlatLoginRequestDto {
    private String userId;
    private String password;

    public KlatLoginRequestDto() {}


    public KlatLoginRequestDto(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
