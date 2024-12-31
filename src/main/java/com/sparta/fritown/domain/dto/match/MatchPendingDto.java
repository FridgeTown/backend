package com.sparta.fritown.domain.dto.match;


import lombok.Getter;

@Getter
public class MatchPendingDto {
    private final Long matchId;
    private final Long challengedBy;
    private final Long challengedTo;
    private final String status;

    public MatchPendingDto(Long matchId, Long challengedBy, Long challengedTo, String status) {
        this.matchId = matchId;
        this.challengedBy = challengedBy;
        this.challengedTo = challengedTo;
        this.status = status;
    }
}
