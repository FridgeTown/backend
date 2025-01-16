package com.sparta.fritown.domain.dto.vote;

import com.sparta.fritown.domain.entity.enums.Votes;
import lombok.Getter;

@Getter
public class SimpleVoteRequestDto {
    private String channelId;
    private Votes votes;
}
