package com.sparta.fritown.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String userRole;

    private String nickname;

    // 생성자
    public User(String email, String userRole, String nickname) {
        this.email = email;
        this.userRole = userRole;
        this.nickname = nickname;
    }
}