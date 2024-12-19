package com.sparta.fritown.user.service;

import com.sparta.fritown.user.dto.RegisterRequestDto;
import com.sparta.fritown.user.entity.User;
import com.sparta.fritown.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User(
                requestDto.getEmail(),
                requestDto.getName(),
                requestDto.getProfileImage(),
                requestDto.getProvider(),
                passwordEncoder.encode("defaultPassword"),
                requestDto.getRole()
        );
        log.info("userService_register called");
        return userRepository.save(user);
    }
}
