package com.sparta.fritown.domain.dto.chat.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatLoginRequestDto {
    private String userId;
    private String password;
}
