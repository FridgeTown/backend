package com.sparta.fritown.domain.entity;

import com.sparta.fritown.domain.entity.enums.Status;
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
    private Status status;

    private String place;

    @ManyToOne
    @JoinColumn(name = "CHALLENGED_TO_ID")
    private User challengedTo;

    @ManyToOne
    @JoinColumn(name = "CHALLENGED_BY_ID")
    private User challengedBy;

    @Builder
    public Matches(User challengedBy, User challengedTo, String place, LocalDate date, String title) {
        this.challengedBy = challengedBy;
        this.challengedTo = challengedTo;
        this.place = place;
        this.date = date;
        this.title = title;
        this.status = Status.PENDING;
        this.streamingTime = new Timestamp(System.currentTimeMillis());
        this.totalRounds = 0;
    }

    public Matches() {

    }
}
