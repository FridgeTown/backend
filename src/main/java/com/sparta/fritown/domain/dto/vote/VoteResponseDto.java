package com.sparta.fritown.domain.dto.vote;

import lombok.Getter;

@Getter
public class VoteResponseDto {
    private final Long matchId;
    private final Long userId;

    public VoteResponseDto(Long matchId, Long userId) {
        this.matchId = matchId;
        this.userId = userId;
    }
}
