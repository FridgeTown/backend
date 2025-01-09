package com.sparta.fritown.domain.dto.vote;

import lombok.Getter;

@Getter
public class VoteResponseDto {
    private final Long matchId;
    private final String playerNickname;

    public VoteResponseDto(Long matchId, String playerNickname) {
        this.matchId = matchId;
        this.playerNickname = playerNickname;
    }
}
