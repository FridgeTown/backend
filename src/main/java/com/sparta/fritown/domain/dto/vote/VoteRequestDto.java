package com.sparta.fritown.domain.dto.vote;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VoteRequestDto {

    @NotNull(message = "매치 ID는 필수입니다")
    private final Long matchId;
    @NotNull(message = "플레이어 닉네임은 필수입니다.")
    private final String playerNickname;

    public VoteRequestDto(Long matchId, String playerNickname) {
        this.matchId = matchId;
        this.playerNickname = playerNickname;
    }
}
