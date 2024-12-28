package com.sparta.fritown.domain.dto.user;

import lombok.Getter;

@Getter
public class OpponentDto {

    private Long userId;
    private String nickname;
    private Integer height;
    private Integer weight;
    private String bio;
    private String gender;
    private String profileImg;

    public OpponentDto(Long userId, String nickname, Integer height, Integer weight, String bio, String gender, String profileImg) {
        this.userId = userId;
        this.nickname = nickname;
        this.height = height;
        this.weight = weight;
        this.bio = bio;
        this.gender = gender;
        this.profileImg = profileImg;
    }
}
