package com.sparta.fritown.domain.entity;

import com.sparta.fritown.domain.dto.user.RegisterRequestDto;
import com.sparta.fritown.domain.entity.enums.Gender;
import com.sparta.fritown.domain.entity.enums.WeightClass;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String provider;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

    private Integer weight;

    private Integer height;

    private String bio;

    private Integer points;

    private Integer heartBeat;

    private Integer punchSpeed;

    private Integer kcal;

    @Enumerated(EnumType.STRING)
    private WeightClass weightClass;

    private String role;

    @Column(nullable = false, unique = true)
    private String nickname;

    @OneToMany(mappedBy = "users")
    private List<UserMatch> userMatches = new ArrayList<>();

    @OneToMany(mappedBy = "challengedTo")
    private List<Matches> challengedTo = new ArrayList<>();

    @OneToMany(mappedBy = "challengedBy")
    private List<Matches> challengedBy = new ArrayList<>();

    public User() {
    }


    public User(RegisterRequestDto requestDto, WeightClass weightClass) {
        this.email = requestDto.getEmail();
        this.provider = requestDto.getProvider();
        this.profileImg = requestDto.getProfileImage();
        this.gender = requestDto.getGender();
        this.age = requestDto.getAge();
        this.weight = requestDto.getWeight();
        this.height = requestDto.getHeight();
        this.bio = requestDto.getBio();
        this.points = 0;
        this.heartBeat = 0;
        this.punchSpeed = 0;
        this.kcal = 0;
        this.role = requestDto.getRole();
        this.nickname = requestDto.getNickname();
        this.weightClass = weightClass;
    }


    public void setProfileImg(String imageFileName) {
        this.profileImg = imageFileName;
    }

    public void setWeightClass(WeightClass weightClass) {
        this.weightClass = weightClass;
    }

    public void resignation() {
        this.email = "123";
        this.nickname = "undefined_user";
        this.profileImg = null;
    }
}