package com.sparta.fritown.domain.dto.vote;

import com.sparta.fritown.domain.entity.Vote;
import com.sparta.fritown.domain.entity.enums.Votes;
import lombok.Getter;

@Getter
public class SimpleVoteResponseDto {
    private Long blueCnt;
    private Long redCnt;

    public SimpleVoteResponseDto(Long blueCnt, Long redCnt) {
        this.blueCnt = blueCnt;
        this.redCnt = redCnt;
    }
}
