package com.sparta.fritown.domain.dto.user;

import com.sparta.fritown.domain.entity.enums.WeightClass;
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
    private WeightClass weightClass;

    public OpponentDto(Long userId, String nickname, Integer height, Integer weight, String bio, String gender, String profileImg, WeightClass weightClass) {
        this.userId = userId;
        this.nickname = nickname;
        this.height = height;
        this.weight = weight;
        this.bio = bio;
        this.gender = gender;
        this.profileImg = profileImg;
        this.weightClass = weightClass;
    }
}
