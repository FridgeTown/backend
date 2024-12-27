package com.sparta.fritown.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_MATCH_ID")
    private UserMatch userMatch;

    private Integer roundNum;

    private Integer kcal;

    private Integer heartBeat;

    private Integer punchNum;

}
