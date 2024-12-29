package com.sparta.fritown.domain.dto.chat.user;

import com.sparta.fritown.domain.dto.chat.user.component.ChatUserDto;
import lombok.Getter;

@Getter
public class CreateUserResponseDto {

    private ChatUserDto chatUserDto;
    private String loginToken;
}




