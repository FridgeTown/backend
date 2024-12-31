package com.sparta.fritown.domain.dto.match;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.entity.enums.Status;
import lombok.Getter;

@Getter
public class MatchPendingDto {
    private final Long matchId;
    private final Long challengedBy;
    private final String nickName;
    private final Integer height;
    private final Integer weight;
    private final String profileImg;
    private final Status status;

    public MatchPendingDto(Matches match, User challengedBy, String profileImg) {
         this.matchId = match.getId();
         this.challengedBy = challengedBy.getId();
         this.nickName = challengedBy.getNickname();
         this.height = challengedBy.getHeight();
         this.weight = challengedBy.getWeight();
         this.profileImg = profileImg;
         this.status = match.getStatus();
    }
}
