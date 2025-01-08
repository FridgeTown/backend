package com.sparta.fritown.domain.dto.vote;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VoteRequestDto {

    @NotNull(message = "매치 ID는 필수입니다")
    private final Long matchId;
    @NotNull(message = "유저 ID는 필수입니다.")
    private final Long userId;

    public VoteRequestDto(Long matchId, Long userId) {
        this.matchId = matchId;
        this.userId = userId;
    }
}
