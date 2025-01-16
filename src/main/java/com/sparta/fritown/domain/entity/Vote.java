package com.sparta.fritown.domain.entity;

import com.sparta.fritown.domain.entity.enums.Votes;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String channelId;
    private Long redCnt = 0L;
    private Long blueCnt = 0L;

    public Vote(){}
    public Vote(String channelId) {
        this.channelId = channelId;
    }

    public void voting(Votes votes) {
        if (votes.equals(Votes.BLUE)) {
            blueCnt += 1;
        } else {
            redCnt += 1;
        }
    }
}
