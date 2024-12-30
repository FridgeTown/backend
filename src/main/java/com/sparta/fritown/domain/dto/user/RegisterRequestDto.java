package com.sparta.fritown.domain.dto.user;

import com.sparta.fritown.domain.entity.enums.Gender;
import com.sparta.fritown.domain.entity.enums.WeightClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private String email;
    private String provider;
    private String profileImage;
    private Gender gender;
    private Integer age;
    private Integer weight;
    private Integer height;
    private String bio;
    private String role;
    private String nickname;
    private String idToken;


    public RegisterRequestDto(String email, String provider, String defaultName, String role) {
        this.email = email;
        this.provider = provider;
        this.nickname = defaultName;
        this.role = role; // default role
    }
}
