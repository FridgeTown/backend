package com.sparta.fritown.domain.dto.user;

import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.entity.enums.Gender;
import com.sparta.fritown.domain.entity.enums.WeightClass;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private Long id;
    private String email;
    private String provider;
    private String profileImg;
    private Gender gender;
    private Integer age;
    private Integer weight;
    private Integer height;
    private String bio;
    private Integer points;
    private Integer heartBeat;
    private Integer punchSpeed;
    private Integer kcal;
    private WeightClass weightClass;
    private String role;
    private String nickname;
    private String accessToken;

    public LoginResponseDto(){}
    public LoginResponseDto(User user, String token) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.profileImg = user.getProfileImg();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.weight = user.getWeight();
        this.height = user.getHeight();
        this.bio = user.getBio();
        this.points = user.getPoints();
        this.heartBeat = user.getHeartBeat();
        this.punchSpeed = user.getPunchSpeed();
        this.kcal = user.getKcal();
        this.weightClass = user.getWeightClass();
        this.role = user.getRole();
        this.nickname = user.getNickname();
        this.accessToken = token;
    }
}
