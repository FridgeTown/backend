package com.sparta.fritown.domain.entity;

import com.sparta.fritown.domain.entity.enums.Gender;
import com.sparta.fritown.domain.entity.enums.WeightClass;
import jakarta.persistence.*;
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

    private String role; /// 임시
    private String nickname; // 임시

    @OneToMany(mappedBy = "user")
    private List<UserMatch> userMatches = new ArrayList<>();

    @OneToMany(mappedBy = "challengedTo")
    private List<Matches> challengedTo = new ArrayList<>();

    @OneToMany(mappedBy = "challengedBy")
    private List<Matches> challengedBy = new ArrayList<>();

    public User() {
    }
    // 생성자
    public User(String email, String role, String nickname, String profileImage, String provider) {
        this.email = email;
        this.profileImg = profileImage;
        this.provider = provider;
    }

    public User(String s, String hihi, String naver) { //// test ****
        this.email = s;
        this.profileImg = hihi;
        this.provider = naver;
    }
}