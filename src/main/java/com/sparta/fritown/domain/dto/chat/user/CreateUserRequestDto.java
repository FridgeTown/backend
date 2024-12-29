package com.sparta.fritown.domain.dto.chat.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CreateUserRequestDto {

    private String userId;             // 필수 필드
    private String password;           // 필수 필드
    private String username;           // 선택 필드
    private String profileImageUrl;    // 선택 필드
    private Map<String, Object> data;  // 선택 필드



}
