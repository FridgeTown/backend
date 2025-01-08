package com.sparta.fritown.domain.dto.chat;

import lombok.Getter;

import java.util.List;

@Getter
public class KlatAddChannelMemberDto {
    List<String> members;

    public KlatAddChannelMemberDto(List<String> members) {
        this.members = members;
    }
}
