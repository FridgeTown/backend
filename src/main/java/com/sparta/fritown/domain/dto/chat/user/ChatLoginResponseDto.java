package com.sparta.fritown.domain.dto.chat.user;

import com.sparta.fritown.domain.dto.chat.user.component.ChatUserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatLoginResponseDto {
    private ChatUserDto chatUserDto; // 사용자 정보
    private String loginToken; // 로그인 토큰
}
