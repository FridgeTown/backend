package com.sparta.fritown.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedUserDto {
    private String nickname;
    private int height;
    private int weight;
    private String bio;
    private String gender; // MALE or FEMALE
}
