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

    @Column(nullable = false)
    private String role;


    private String nickname;

    private String profileImage;
    private String provider;
    private String password;

    public User() {
    }
    // 생성자
    public User(String email, String role, String nickname, String profileImage, String provider, String password) {
        this.email = email;
        this.role = role;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.provider = provider;
        this.password = password;
    }

    public User(String email, String provider) {
        this.email = email;
        this.provider = provider;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}