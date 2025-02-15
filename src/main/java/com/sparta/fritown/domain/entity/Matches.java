package com.sparta.fritown.domain.entity;

import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
@Getter
public class Matches {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp streamingTime;
    private Integer totalRounds;
    private String title;
    private LocalDate date;

    @OneToMany(mappedBy = "matches")
    private List<UserMatch> userMatches = new ArrayList<>();

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    private String place;

    @ManyToOne
    @JoinColumn(name = "CHALLENGED_TO_ID")
    private User challengedTo;

    @ManyToOne
    @JoinColumn(name = "CHALLENGED_BY_ID")
    private User challengedBy;

    @Column(nullable = false)
    private Long viewNum = 0L;

    private String thumbNail;

    private String channelId; // 1:1 채팅방 아이디

    @Builder
    public Matches(User challengedBy, User challengedTo, String place, String title, LocalDate date) {
        this.challengedBy = challengedBy;
        this.challengedTo = challengedTo;
        this.place = place;
        this.title = title;
        this.date = date;
        this.status = Status.PENDING;
    }

    public Matches() {

    }

    public void updateStatus(Status status) {
        this.status = status;
    }
    public Matches(User challengedTo, User challengedBy, Status status) {
        this.challengedTo = challengedTo;
        this.challengedBy = challengedBy;
        this.status = status;
    }

    public void validateMatchedUserId(Long userId) {
        if(!challengedBy.getId().equals(userId) && !challengedTo.getId().equals(userId)) {
            throw ServiceException.of(ErrorCode.USER_NOT_PART_OF_MATCH);
        }
    }

    public boolean isValidHistory(LocalDate todayDate) {
        return this.date.isBefore(todayDate) && this.status.equals(Status.DONE);
    }
    public boolean isValidFuture(LocalDate todayDate) {
        return this.date.isAfter(todayDate) && this.status.equals(Status.ACCEPTED);
    }

    public void setTitle(String userName, String opponentName) {
        this.title = userName + "vs" + opponentName;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void incrementViewNum()
    {
        this.viewNum++;
    }

    public void decrementViewNum() {

        if(this.viewNum <=0)
        {
            throw ServiceException.of(ErrorCode.VIEW_COUNT_CANNOT_BE_NEGATIVE);
        } else {
            this.viewNum--;
        }
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    // 상대방 유저를 반환하는 메서드 자신의 userId를 넣으면 상대방 User entity를 반환
    public User getOpponent(Long userId)
    {
        if (challengedBy != null & challengedBy.getId().equals(userId)) {
            return challengedTo;
        } else if (challengedTo != null && challengedTo.getId().equals(userId)) {
            return challengedBy;
        } else {
            throw ServiceException.of(ErrorCode.USER_NOT_PART_OF_MATCH);
        }

    }
}
