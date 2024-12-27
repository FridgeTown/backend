package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.RegisterRequestDto;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        System.out.println(requestDto.getRole());
        User user = new User(
                requestDto.getEmail(),
                requestDto.getRole(),
                requestDto.getName(),
                requestDto.getProfileImage(),
                requestDto.getProvider()
        );

//        this.email = email;
//        this.role = role;
//        this.nickname = nickname;
//        this.profileImage = profileImage;
//        this.provider = provider;
//        this.password = password;
        log.info("userService_register called");
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw ServiceException.of(ErrorCode.USER_NOT_FOUND);
        }
        return user.get();
    }
}
