package com.sparta.fritown.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class UserMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USERS_ID")
    private User user;

    @OneToMany(mappedBy = "userMatch")
    private List<Round> rounds = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "MATCH_ID")
    private Matches matches;

    private Boolean winner;

    public UserMatch() {
    }
    public UserMatch(Matches newMatch, User user) {
        this.user = user;
        this.matches = newMatch;
    }
}
