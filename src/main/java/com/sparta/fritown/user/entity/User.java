package com.sparta.fritown.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String role;

    private String nickname;

    private String profileImage;
    private String provider;

    public User() {
    }
    // 생성자
    public User(String email, String role, String nickname, String profileImage, String provider) {
        this.email = email;
        this.role = role;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.provider = provider;
    }
}