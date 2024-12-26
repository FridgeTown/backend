package com.sparta.fritown.domain.entity;

import com.sparta.fritown.domain.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Date;
import java.sql.Timestamp;
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
    private Date date;

    @OneToMany(mappedBy = "matches")
    private List<UserMatch> userMatches = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    private String place;

    @ManyToOne
    @JoinColumn(name = "CHALLENGED_TO_ID")
    private User challengedTo;

    @ManyToOne
    @JoinColumn(name = "CHALLENGED_BY_ID")
    private User challengedBy;


}
